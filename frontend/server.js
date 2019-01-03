// =====imports=====
const http = require('http');
const Router = require('router');
const finalhandler = require('finalhandler');
const fs = require('fs');
const atob = require('atob');
const MongoClient = require('mongodb').MongoClient;
const querystring = require('querystring');

// =====ressources=====
const style = "./pages/main.css";
const script = "./pages/main.js";
const landing = "./pages/index.html";

//=====Database=====
const mongoUrl = "mongodb://127.0.0.1/";
const mongoDatabase = "test";
const mongoCollectionUsers = "users";
const mongoCollectionBoxes = "boxes";

//return this in case an error occurs
const _500 =    "<html><head><title>An Error Occurred</title></head>"+
                "<body><h1>Too Bad</h1></body></html>";

//=====Helper functions=====
//Erzeugt einen zufälligen String mit saltLength Zeichen
function saltGenerator(saltLength = 16){
    
    // TODO: Sicherstellen dass kein Backslash dabei ist

    // Obere- und untere Grenze, druckbare ASCII Zeichen
    const lowerBound = 33;
    const upperBound = 125;

    // Übergabeparameter überprüfen
    //Sicherstellen dass saltLength eine Zahl ist und mind. 16 ist
    if(typeof saltLength !== "number" || saltLength < 16){
        saltLength = 16;
    }

    // abrunden, um sicher zu stellen dass saltLength eine ganze Zahl ist
    return new Array(Math.floor(saltLength))
    // Array mit Nullen auffüllen
    .fill(0)
    // Zufallszahlen erzeugen und in ASCII Zeichen umwandeln
    .map(x => String.fromCharCode(Math.floor(lowerBound + Math.random()*(upperBound - lowerBound))))
    // Einzelne Zeichen konkatenieren
    .reduce((str,char)=>{return str + char});
}


//=====Routes=====
const router = Router();

//return landing page
router.get('/', function(request, response){
    fs.readFile(landing,(error, data)=>{
        if(error){
            response.statusCode = 500;
            response.end();
            throw error;
            
        }
        response.writeHead(200, {"Content-Type":"text/html","Content-Length":data.length});
        response.write(data);
        response.end();
    });
});
//send javascript
router.get("/main.js", function(request,response){
    fs.readFile(script,function(error,data){
        if(error){
            response.statusCode = 500;
            response.end();
            throw error;
            
        }
        response.writeHead(200,{"Content-Type":"text/javascript", "Content-Length": data.length})
        response.write(data)
        response.end()
    })
});
//send css
router.get("/main.css",function(request,response){
    fs.readFile(style,function(error,data){
        if(error){
            response.statusCode = 500;
            response.end();
            throw error;
            
        }
        response.writeHead(200,{"Content-Type":"text/css", "Content-Length": data.length});
        response.write(data);
        response.end()
    })
})

//login
// Authorization field must be set in the request header
router.get("/login",function(request,response){
    
    //get Authorization Header
    //Check if Authorization Header is set
    if(request.headers.authorization){
        
        const authorization = request.headers.authorization;
        //Split encoded Authorization Header
        const splittedAuthorization = authorization.split(" ");
        // Decoded Credentials are the first element
        const credentials = splittedAuthorization[1];
        //decode Credentials
        const decodedAuth = atob(credentials);
        //split credentials        
        const splittedCredentials = decodedAuth.split(":");
        const username = splittedCredentials[0];
        const pass = splittedCredentials[1];

        //TODO: salt submitted password, hash it and compare it to stored hash

        //Check database for Combination
        MongoClient.connect(mongoUrl,function(error,database){
            if(error){
                throw error;
            }
            const dbo = database.db(mongoDatabase);
            
            const user = dbo.collection(mongoCollectionUsers)
                .findOne({name:username,password:pass});
            
            //const u = user.clone();
            
            //TODO check for empty Object, findOne returns an empty Object if no Document matches instead of null
            if(user){
                user
                //Promise resolved
                .then((result)=>{
                    response.writeHead(200,{"Content-Type":"text/plain"});
                    response.end(result._id.toString());
                },
                //Promise rejected
                () =>{console.log("Promise rejected")})
            }else{
                console.log("user was empty")
                response.statusCode = 500;
                response.end(_500);
            }
        })
    //If no Authorization Header is set
    }else{
        //TODO: return unathorized instead
        response.statusCode = 500;
        response.setHeader("Content-Type","text/html")
        response.end(_500);
    }
    
    //if combination exists return the token (in this case the user id)
});

// Sign Up new users
router.post('/users',function(request,response){
    
    let body = ''
    request.on('data',function(data){
        body += data.toString();
    });
    //The request body was received
    request.on('end',function(){
        //Parse URL encoded String to Object
        const query = querystring.parse(body);
        console.log(query);
        saltGenerator(16);
       
        console.log(salt);
        // Add Salt to query so that it'll be saved in the db
        query.salt = saltGenerator(16);

        //Concatenate the user-entered password and salt
        const saltetPassword = query.password + query.salt

        //TODO: Hash salted password and save it to query object so it'll be saved to db
        console.log(query)

        MongoClient.connect(mongoUrl,function(error,db){
            if(error) throw error;
            const dbo = db.db(mongoDatabase);
            //TODO: Check if Username is unique
            dbo.collection(mongoCollectionUsers).insertOne(query,function(error,res){
                if(error){
                    //TODO: send response to Client
                    throw error}
                else{
                    response.statusCode = 200;
                    //TODO: generate token and return that token to client so that client can be forwarded to his main page with logged in state
                    response.end();
                }
            });
        })
        
    });   
});

//Query free boxes
router.get('/boxes', function(request,response){
    MongoClient.connect(mongoUrl,function(error,db){
        if(error) throw error;
        const dbo = db.db(mongoDatabase);
        //Query Database for vacant boxes, convert result to array
        dbo.collection(mongoCollectionBoxes).find({status:'vacant'}).toArray(function(error,items){
            if(error) throw error;
            console.log(items);
            response.writeHead(200,{"Content-Type":"application/JSON"});
            //Convert JSON-Array to String and send to Client
            response.end(JSON.stringify(items));
        });
    })
})


const server = http.createServer(function(request, response){
    response.setHeader('Access-Control-Allow-Origin', '*');
    response.setHeader('Access-Control-Request-Method', '*');
	response.setHeader('Access-Control-Allow-Methods', 'OPTIONS, GET');
	response.setHeader('Access-Control-Allow-Headers', '*');
    router(request, response, finalhandler(request, response));
});

server.listen(8080);


