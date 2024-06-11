const express = require("express");
const app = express();
const port = 3000;
const sqlite3 = require("sqlite3");
const bcrypt = require("bcrypt");

const db = new sqlite3.Database("./voters.db", (err) => {
  if (err) {
    console.log("faild to connect to the db");
  } else {
    console.log("connected to the db voters");
  }
});

const jwt = require("jsonwebtoken");
app.use(express.json());

var databaseStruct = {
  atStart:
    "CREATE TABLE PEP(Name VARCHAR(50),Email VARCHAR(50),Password VARCHAR(50),Admin INT,Voted INT,Phone INT,Session VARCHAR(50))",
  VoteTable: "CREATE TABLE VOTES(Name VARCHAR(50),VoteCount int)",
  RecordTable: "CREATE TABLE REC (Email VARCHAR(50), Party VARCHAR(50))"

};


// db.all(databaseStruct.RecordTable, (err) => {
//     if (err) {
//         console.log(err)
//     }
// })

const CreateRecord = (Party,Email) => {
    console.log(Party)
    db.run(
        "INSERT INTO REC (Email,Party) VALUES (?, ?)",
        [Email,Party],
        (err) => {
            return false
        })
    return true

}


app.post("/toggleadmin", (req, res) => {
  const { key, name, isadmin } = req.body;
  if (!key || !name || !isadmin) {
    // return res.status(400).json({ error: "invalid entrys or loose entry" });
  }
  if(key=="KokoEatingBananas"){
      let stat = isadmin ? 1 : 0;
      let data = [1, name];
      let querry = "UPDATE TABLE PEP SET Admin = ? WHERE Name = ?";
      db.run(querry, data, (err) => {
        if (err) {
        //   return res.status(501).json({ error: "Internal Server Error" });
        }
    });
    return res.status(200).json({message:"Success"})
  }
//   return res.status(401).json({ error: "invalid entrys or loose entry" });


});

app.post("/register", async (req, res, next) => {
  const { name, email, password, phone } = req.body;
  if (!name || !email || !password || !phone) {
    return res.status(409).json({ error: "Argument Error Occured" });
  }
  console.log(name, email, password);
  // if exists
  db.get("SELECT * FROM PEP WHERE email = ?", email, (err, row) => {
    if (err) {
      console.error("Error querying database:", err.message);
      return res.status(500).json({ error: "Server Crashed contact admins" });
    }

    if (row) {
      // Email already exists
      return res.status(409).json({ error: "Email already registered" });
    }

    try {
      bcrypt.genSalt(10, (err, salt) => {
        bcrypt.hash(password, salt, (err, hash) => {
          if (err) {
            console.error("Error hashing password:", err.message);
            return res
              .status(500)
              .json({ error: "Server Crashed contact admins" });
          }

          // Insert the new user into the database
          db.run(
            "INSERT INTO PEP (Name, Email, Password,Admin,Voted,Phone,Session) VALUES (?, ?, ?, ?, ?, ?, ?)",
            [name, email, hash, 0, 0, phone, ""],
            (err) => {
              if (err) {
                console.error("Error inserting user:", err.message);
                return res
                  .status(500)
                  .json({ error: "Server Crashed contact admins" });
              }
              res
                .status(200)
                .json({ message: "user registerd Succesfully " + name });
            }
          );
        });
      });
    } catch (e) {
      return res.status(501).json({ error: "internal Server Error" });
    }
  });
});

const tokenCreator = (email) => {
  let token;
  try {
    token = jwt.sign(
      {
        email: email,
      },
      "soemthisngdgjspfjosdjfpfkonmdpfom",
      { expiresIn: "1h" }
    );
  } catch (err) {
    const error = new Error("Error! Something went wrong.");
    return next(error);
  }

  return {
    success: true,
    data: {
      email: email,
      token: token,
    },
  };
};



app.post("/login", (req, res) => {
  const { email, password } = req.body;
  console.log(email, password);

  db.get("SELECT * FROM PEP WHERE email = ?", email, (err, row) => {
    if (err) {
      return res.status(401).json({ error: "Incorrect email or password" });
    } else if (row) {
      let dbpassword = row.Password;
      console.log(dbpassword);

      // res.status(201).json({ "message": "ok" })
      bcrypt.compare(password, dbpassword, (err, rep) => {
        if (err) {
          console.log("passwords dont mtch:", err.message);
          return res.status(401).json({ error: "Incorrect email or password" });
        }
        if (!rep) {
          return res.status(401).json({ error: "Incorrect email or password" });
        }

        const token = tokenCreator(email);
        // console.log(token.data.token, token.data.email)
        let admin = row.Admin == 0 ? "false" : "true";
        return res.status(201).json({
          admin: admin,
          token: token.data.token,
        });
      });
    } else {
      return res.status(401).json({ error: "Incorrect email or password" });
    }
  });
});

app.post('/record',(req,res)=>{
    const{email} = req.body
    let querry = "SELECT Party from Rec WHERE Email = ?"
    let data = [email]
    db.get(querry,data,(err,row)=>{
        if(err){
            res.status(501).json({"message":"Internal server error"})
        }
        if(row){
            let party = row.Party
            res.status(200).json({"party":party});
        }
    })


})


app.post("/addcandidate", (req, res) => {
  const { email, password, candidate } = req.body;
  console.log(email, password, candidate);
  // res.status(200).json({ "ok": "ok" })

  db.get("SELECT * FROM PEP WHERE email = ?", email, (err, row) => {
    //just add admin = 1 and set admin manually later
    if (err) {
      return res.status(401).json({ error: "Incorrect email or password" });
    } else if (row) {
      let dbpassword = row.Password;
      console.log(dbpassword);

      // res.status(201).json({ "message": "ok" })
      bcrypt.compare(password, dbpassword, (err, rep) => {
        if (err) {
          console.log("passwords dont mtch:", err.message);
          return res.status(401).json({ error: "Incorrect email or password" });
        }
        if (!rep) {
          return res.status(401).json({ error: "Incorrect email or password" });
        }
        for (let i = 0; i < candidate.length; i++) {
          db.run(
            "INSERT INTO Votes (Name,VoteCount) VALUES (?, ?)",
            [candidate[i], 0],
            (err) => {
              if (err) {
                console.error("Error inserting user:", err.message);
                return res
                  .status(500)
                  .json({ error: "Server Crashed contact admins" });
              }
            }
          );
        }
        res.status(200).json({
          message: ` ${candidate.length} candidates added succesfully `,
        });
      });
    } else {
      return res.status(401).json({ error: "Incorrect email or password" });
    }
  });
});

app.post("/viewvotes", (req, res) => {
  const { email, password } = req.body;
  console.log(email, password);
  // res.status(200).json({ "ok": "ok" })

  db.get("SELECT * FROM PEP WHERE email = ? ", email, (err, row) => {
    //just add admin = 1 and set admin manually later
    if (err) {
      return res.status(401).json({ error: "Incorrect email or password" });
    } else if (row) {
      let dbpassword = row.Password;
      console.log(dbpassword);

      // res.status(201).json({ "message": "ok" })
      bcrypt.compare(password, dbpassword, (err, rep) => {
        if (err) {
          console.log("passwords dont mtch:", err.message);
          return res.status(401).json({ error: "Incorrect email or password" });
        }
        if (!rep) {
          return res.status(401).json({ error: "Incorrect email or password" });
        }
        let result = [0, 0, 0, 0];
        db.all("SELECT * FROM VOTES", [], (err, rows) => {
          if (err) {
            throw err;
          }
          for (let i = 0; i < rows.length; i++) {
            if (rows[i].Name == "APPDEV") {
              result[0] = rows[i].VoteCount;
            } else if (rows[i].Name == "KOOEF") {
              result[1] = rows[i].VoteCount;
            } else if (rows[i].Name == "fiifj") {
              result[2] = rows[i].VoteCount;
            } else if (rows[i].Name == "fjeifj") {
              result[3] = rows[i].VoteCount;
            }

            // result.push(rows[i])
            // result.push([rows[i].Name, rows[i].VoteCount])
            // console.log({ "party": rows[i].Name, "votes": rows[i].VoteCount })
          }
          console.log(result);
          res.status(200).json({
            part1: result[0].toString(),
            part2: result[1].toString(),
            part3: result[2].toString(),
            part4: result[3].toString(),
          });
        });
      });
    } else {
      return res.status(401).json({ error: "Incorrect email or password" });
    }
  });
});

const incrementVotes = (Party) => {
  var count = 0;
  db.all("SELECT VoteCount FROM VOTES WHERE NAME = ?", [Party], (err, row) => {
    if (err) {
      console.error(err.message);
    }

    count = parseInt(row[0].VoteCount) + 1;

    console.log(count, "out side");
    let sql = `UPDATE VOTES
        SET VoteCount = ? 
        WHERE Name = ?`;

    let data = [count, Party];

    db.run(sql, data, function (err) {
      if (err) {
        return false;
      }
      console.log(`Row(s) updated: ${this.changes}`);
      return true;
    });
  });
};

app.get("/test", (req, res) => {
  console.log(incrementVotes("KOOEF"));
  res.status(200).json({ "": "ok" });
});

app.post("/vote", (req, res) => {
  const { email, password, Party } = req.body;
  console.log(email, password);

  db.get("SELECT * FROM PEP WHERE email = ?", email, (err, row) => {
    if (err) {
      return res.status(401).json({ error: "Incorrect email or password" });
    } else if (row) {
      let party = ["APPDEV", "KOOEF", "fiifj", "fjeifj"];
      db.get("SELECT Voted FROM PEP WHERE email = ?", [email], (err, row) => {
        let voted = row.Voted;
        if (voted == 0) {
          for (let i = 0; i < party.length; i++) {
            if (party[i] == Party) {
              let data = [1, email];
              let sql = `UPDATE PEP
                        SET Voted = ?
                        WHERE Email = ? `;

              db.run(sql, data, function (err) {
                if (err) {
                  return console.error(err.message);
                }
                console.log(`Row(s) updated: ${this.changes}`);
              });
              incrementVotes(party[i]);

              CreateRecord(party[i],email)

              res.status(200).json({ message: "you have succesfully Voted" });
            }
          }
        }
        
        else {
          res
            .status(200)
            .json({ message: "you have alerady submitted the vote" });
        }
      });
    } else {
      return res.status(401).json({ error: "Incorrect email or password" });
    }
  });
});

app.post("/jwtvote", (req, res) => {
  const { token, Party } = req.body;
  if (!token || !Party) {
    return res.status(401).json({ error: "empty email or password" });
  }
  console.log(token, Party);
  let data;
  try {
    data = jwt.verify(token, "soemthisngdgjspfjosdjfpfkonmdpfom");
  } catch (er) {
    console.error(er);
  }
  if (Date.now() >= data.exp * 1000) {
    return res.status(401).json({ error: "Session has Ended" });
  }
  let email = data.email;
  db.get("SELECT * FROM PEP WHERE email = ?", email, (err, row) => {
    if (err) {
      return res.status(401).json({ error: "Incorrect email or password" });
    } else if (row) {
      let party = ["APPDEV", "KOOEF", "fiifj", "fjeifj"];
      db.get("SELECT Voted FROM PEP WHERE email = ?", [email], (err, row) => {
        let voted = row.Voted;
        if (voted == 0) {
          for (let i = 0; i < party.length; i++) {
            if (party[i] == Party) {
              let data = [1, email];
              let sql = `UPDATE PEP
                                            SET Voted = ?
                                            WHERE Email = ?`;

              db.run(sql, data, function (err) {
                if (err) {
                  return console.error(err.message);
                }
                console.log(`Row(s) updated: ${this.changes}`);
              });
              incrementVotes(party[i]);

              res.status(200).json({ message: "you have succesfully Voted" });
            }
          }
        } else {
          res
            .status(200)
            .json({ message: "you have alerady submitted the vote" });
        }
      });
    } else {
      return res.status(401).json({ error: "Incorrect email or password" });
    }
  });

  // res.status(200).json({"ok":"pk"})
});

app.listen(port, "192.168.137.214", (err) => {
  if (err) {
    console.log("server failed to start");
  }
});
//Connecting to the database
