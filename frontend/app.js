// PImpports
const express = require('express');
const MongoClient = require('mongodb').MongoClient
const bodyParser = require('body-parser');
const cookieParser = require('cookie-parser');

//Constants
const PORT = 3000;
const MONGOCONNECT = 'mongodb://127.0.0.1' //Database URL
const MONGOUSERS = 'users';  //Users Collection
const MONGODB = 'test'; //Database
const MONGOSESSION = 'sessions'; // Session Collection
const MONGOBOXES = 'boxes'; // boxes Collection


const app = express();

app.use(express.static('public'));

// Routes

app.set('view engine', 'ejs')
app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());
app.use(cookieParser());


//Log in
app.post('/user',function(req, res){
    let email = req.body.email;
    let password = req.body.password;

    console.log(email, password)

    //console.log(req)
    MongoClient.connect(MONGOCONNECT,{useNewUrlParser: true}, function(err, db){
        if(err) throw err;
        
        const dbo = db.db(MONGODB);

        const user = dbo.collection(MONGOUSERS)
        .findOne({email:email,password:password});

        user
        .then((result=>{
            if(result){
                //register session with server
                // User-id is SessionId for now
                let id = result._id.valueOf().toString();
                //Debugging
                console.log(`session id: ${id}`, typeof id);
                //Insert into session collection
                dbo.collection(MONGOSESSION)
                .insertOne({'session_id':id,'session_time':Date.now()});

                //return session_id to client, client has to send session_id with every request
                res.cookie('sessionId',id);
                //Does not work yet
                //res.writeHead(200,{'Content-Type':'text/plain'});
                res.render('pages/boxenHome');

                //TODO: Get Corresponding Boxes from Database and render view
                
            } else {
                res.writeHead(500,{'Content-Type': 'text/plain'});
                res.end('Wrong Credentials')
            }
        }))

    });
});

app.get('/rent',function(req,res){
    validateSessionCookie(req,res,function(){
        //res.writeHead(200,{'Content-Type':'text/html'});
        res.render('pages/addBoxen')
    })
})

//register
app.post('/register',function(req,res){
    //console.log(req)
    let id;
    MongoClient.connect(MONGOCONNECT,function(err, db){
        const dbo = db.db(MONGODB);
        const result = dbo.collection(MONGOUSERS).insertOne({email: req.body.email,password: req.body.password1});
        result.then(x=>{
            let id = x.insertedId.valueOf().toString();
            //Session registrieren
            dbo.collection(MONGOSESSION).insertOne({sessionId:id});

            res.setHeader('Set-cookie', 'sessionId='+id);
            res.setHeader('Content-Type','text/html');
            res.statusCode=200;
            res.render('pages/boxenHome')
        });
    })
    //Cookie mit SessionId erstellen,
    // console.log(id);
    // res.writeHead(200,{"Content-Type":"text/plain"})
    // res.end('Stuff happened')
});

//get users boxes, change to get when client supports get
app.post('/boxes',function(req, res){
    validateSessionCookie(req,res,function(){
        console.log('valid session');
        MongoClient.connect(MONGOCONNECT,function(err,db){
            if(err) throw err;
            let result = db.db(MONGODB).collection(MONGOBOXES).find({owner:req.cookies.sessionId}).toArray(function(error, docs){
                console.log(docs);
                res.cookie('sessionId',req.cookies.sessionId)
                //res.writeHead(200,{'Content-Type':'application/json'});
                res.end(JSON.stringify(docs));


            })
        })
    })
    // console.log(req.cookies.sessionId)
    // const sessionId = req.cookies.sessionId
    // console.log("boxes were requested", "sessionId was " + sessionId, typeof sessionId);
    // MongoClient.connect("mongodb://127.0.0.1",{useNewUrlParser: true}, function(err, db){
    //     if(err) throw err;
        
    //     //Check if the provided session_id is valid
    //     const dboSession = db.db('test');

    //     const session = dboSession.collection('sessions')
    //     .findOne({session_id : sessionId});

    //     session.then(result => {
    //         //Check if there was an actual result
    //         if(result){
    //             //The session_id was valid
    //             console.log("valid session id");
        
    //             MongoClient.connect(MONGOCONNECT, {useNewUrlParser: true}, function(err,db){
    //                 if(err) throw err;
    //                 const dbo = db.db(MONGODB);
    //                 //Remember, find returns a collection and not a promise...
    //                 const boxes = dbo.collection(MONGOBOXES).find({owner: sessionId}) //returns all boxes that are not rented out to users
    //                 boxes.toArray(function(error,docs){
    //                     console.log(docs);
    //                     //TODO: Order docs by location
    //                     res.writeHead(200,{'Content-Type':'application/json'});
    //                     res.end(JSON.stringify(docs))
    //                 })
    //             });
    //         } else {
    //             //The session_id was invalid
    //             console.log("invalid seession id")
    //             sessionValid = false

    //         }
    //     })
    //     .catch(err => {console.log(err)})
    // });

});

//get vacant boxes
app.get('/freeBoxes',function(req,res){
    console.log('vacant boxes requested');
    
    //validate session
    validateSessionCookie(req,res,function(){
        //query database for vacant boxes
        MongoClient.connect(MONGOCONNECT,function(err,db){
            if(err) throw err;
            const dbo = db.db(MONGODB);
            result = dbo.collection(MONGOBOXES).find({owner:'null'}).toArray(function(err,docs){
                console.log(docs);
                //Boxen nach Standort sortieren
                let locations = {};
                docs.forEach(element => {
                    if(locations[element.location.address] == undefined){
                        locations[element.location.address] = [];
                    }
                    locations[element.location.address].push(element);
                });
                
                //return json array of empty boxes to client
                res.writeHead(200,{'Content-Type':'application/json'});
                res.end(JSON.stringify(locations))
            // console.log(JSON.stringify(locations))
            });
        })
    })    
})

//rent-a-box
app.put('/rentabox',function(req,res){
    const location = req.body.location;
    console.log('Location: ' + location)
    validateSessionCookie(req,res,function(){
        const user = req.cookies.sessionId;
        console.log(user);
        //Box mieten, dazu in der Datenbank genau ein Element finden, dass die Adresse hat
        MongoClient.connect(MONGOCONNECT,function(err,db){
            if(err) throw err;
            
            //Connect to Database
            const result = db.db(MONGODB)
            //Use Collection boxes
            .collection(MONGOBOXES)
            //update a single Element in that Collection that matches the Location
            //and does not have an oner yet by setting the owner to the current user
            .updateOne({"location.address" : location, owner:'null'}, {$set:{owner:user}});

            //console.log(`Gefundene Boxen: ${result}`);

            

            //return success to user
            // TODO: Prüfen ob ein Fehler aufgetreten ist
            /*
            updateOne gibt ein Promise Objekt zurück, in dem in matchedCount vermerkt ist
            wie viele Elemente betroffen sind und in modifiedCount wie viele geändert wurden
            */
            result.then(r => {
                console.log(r.modifiedCount)
                //Worst Case, es wurden mehrere Objekte gemietet obwohl nur eins gemietet werden sollte
                if(r.modifiedCount > 1){
                    //Diesen Fehler kann man vermutlich vor dem Nutzer verbergen
                }
                //Falls doch keine Box mehr verfügbar war, also matchedCount = 0 oder modifiedCount = 0
                else if(r.modifiedCount == 0 || r.matchedCount == 0){
                    //Fehler senden "Internal Server Error" passt am ehesten
                    res.writeHead(500,{"Content-Type":"application/json"});
                    res.end(JSON.stringify({success:false}));
                }
                //Alles in Ordnung
                else{
                    res.writeHead(200,{"Content-Type":"application/json"});
                    res.end(JSON.stringify({success:true}))        
                }
            })

            // res.writeHead(200,{'Content-Type': 'application/json'})
            // res.end(JSON.stringify({success:true}));

        })
    })
})
// Start Server
app.listen(PORT, function(){
    console.log(`Server started on Port ${PORT}`);
});

function validateSessionCookie(req,res,callback){
    const sessionId = req.cookies.sessionId
    console.log(sessionId)
    MongoClient.connect(MONGOCONNECT, function(err,db){
        if(err) throw err;
        db.db(MONGODB).collection(MONGOSESSION).find({session_id:sessionId}).toArray(function(err,doc){
            if(doc.length == 0){
                //error
                console.log(JSON.stringify(doc))
            } else{
                console.log('all right')
                callback(res);
            }
        })

    })
}