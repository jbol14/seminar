// Imports
const express = require('express');
const MongoClient = require('mongodb').MongoClient
const bodyParser = require('body-parser');
const cookieParser = require('cookie-parser');

//Constants
const PORT = 3000;


const app = express();

app.use(express.static('public'));

// Routes
app.get('/', function(req, res){
    res.send('Hallo Welt');
});

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
    MongoClient.connect("mongodb://127.0.0.1",{useNewUrlParser: true}, function(err, db){
        if(err) throw err;
        
        const dbo = db.db('test');

        const user = dbo.collection('users')
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
                dbo.collection('sessions')
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

//register
app.post('/register',function(req,res){
    //console.log(req)
    MongoClient.connect("mongodb://127.0.0.1",function(err, db){
        const dbo = db.db('test');
        dbo.collection('users').insertOne({email: req.body.email,password: req.body.password1});
    })
    res.writeHead(200,{"Content-Type":"text/plain"})
    res.end('Stuff happened')
});

//get users boxes, change to get when client supports get
app.post('/boxes',function(req, res){
    console.log(req.cookies.sessionId)
    const sessionId = req.cookies.sessionId
    console.log("boxes were requested", "sessionId was " + sessionId, typeof sessionId);
    MongoClient.connect("mongodb://127.0.0.1",{useNewUrlParser: true}, function(err, db){
        if(err) throw err;
        
        //Check if the provided session_id is valid
        const dboSession = db.db('test');

        const session = dboSession.collection('sessions')
        .findOne({session_id : sessionId});

        session.then(result => {
            //Check if there was an actual result
            if(result){
                //The session_id was valid
                console.log("valid session id");
        
                MongoClient.connect("mongodb://127.0.0.1", {useNewUrlParser: true}, function(err,db){
                    if(err) throw err;
                    const dbo = db.db('test');
                    //Remember, find returns a collection and not a promise...
                    const boxes = dbo.collection('boxes').find({owner: sessionId}) //returns all boxes that are not rented out to users
                    boxes.toArray(function(error,docs){
                        console.log(docs);
                        //TODO: Order docs by location
                        res.writeHead(200,{'Content-Type':'application/json'});
                        res.end(JSON.stringify(docs))
                    })
                });
            } else {
                //The session_id was invalid
                console.log("invalid seession id")
                sessionValid = false

            }
        })
        .catch(err => {console.log(err)})
    });

});

//get vacant boxes
app.get('/freeBoxes',function(req,res){
    console.log('vacant boxes requested');
    
    //validate session

    //query database for vacant boxes
    MongoClient.connect('mongodb://127.0.0.1',function(err,db){
        if(err) throw err;
        const dbo = db.db('test');
        result = dbo.collection('boxes').find({owner:'null'}).toArray(function(err,docs){
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
// Start Server
app.listen(PORT, function(){
    console.log(`Server started on Port ${PORT}`);
});