const express = require('express');
const app = express();

const url = "mongodb://127.0.0.1:27017"
const {MongoClient, Timestamp} = require('mongodb');
const client = new MongoClient(url)
const PORT = 3000;

app.use(express.urlencoded({extended:true}));
app.use(express.json());


const checkCurrentAlert = async(req, res) =>{
    try{
        const db = client.db('GazDetector');
        const collection = db.collection('Alerte');
        var query = { zoneId: req.body.zoneId, isFinished: false };
        const result = await collection.find(query)
        const array = await result.toArray();
        if(array.length>0){ 
            res.status(200).json({array});
        } else{
            res.status(201).json({msg:"No Alerts"});
        }
    } catch(error){
        console.log(error);
    }

}

const getPerson = async (req, res) => {
    console.log(req);
    try{
        const person = {
            surname: req.body.surname,
            password: req.body.password,
        }
        const db = client.db('GazDetector');
        const collection = db.collection('Person');
        var query = { surname: person.surname, password: person.password };
        const result = await collection.find(query)
        const array = await result.toArray();
        if(array.length>0){ 
            res.status(200).json({array});
        } else{
            res.status(204).json({msg:"No alerts"});
        }
    } catch(error){
        console.log(error);
    }

}

const addPerson = async(req, res) =>{
    try{
        const newPerson = {
            surname: req.surname,
            name: req.name,
            password: req.password,
            isAdmin: req.isAdmin,
            isPMR: req.isPMR,
            isColorBlind:req.isColorBlind,
            zone: req.zone

        }
        const db = client.db('GazDetector');
        const collection = db.collection('Person');
        let result = await collection.insertOne(newPerson);
        res.status(200).json(result);
    } catch(error){
        console.log(error);
        res.status(500).json(error);
    }
} 

const getAlert = async (req, res) => {
    try{
        const db = client.db('GazDetector');
        const collection = db.collection('Alerte');
        let cursor = await collection.find()
        let result = await cursor.toArray();
        if(result.length>0){
            res.status(200).json(result);
        } else{
            res.status(204).json({msg:"No alerts"});
        }
    } catch(error){
        console.log(error);
        res.status(500).json(error);
    }
}

const addAlert = async (req, res) => {
    try{
        const newAlert = {
            date: new Date().toJSON(),
            zoneId: req.body.idZone,
            typeAlert:req.body.typeAlert,
            idPerson:req.body.alerter,
            gazValue:req.body.gazValue,
            isFinished:false,
            zoneDatas:{
                floor:null,
                x:null,
                y:null,
            }
            
        }
        const db = client.db('GazDetector');
        const collection = db.collection('Alerte');
        let result = await collection.insertOne(newAlert);
        res.status(200).json(result);
    } catch(error){
        console.log(error);
        res.status(500).json(error);
    }
}

const finishAlert = async(req, res) => {
    try{
        const alert = {
            zoneId: req.body.zoneId,
        }
        const db = client.db('GazDetector');
        const collection = db.collection('Alerte');
        const collectionObstacle = db.collection('Obstacle')
        const collectionFPerson = db.collection('FaintedPerson')
        var query = { zoneId: alert.zoneId, isFinished:false};
        var query2 = {zoneId: alert.zoneId}
        const updateDoc = { $set: {isFinished: true}};
        const result = await collection.updateMany(query, updateDoc)
        await collectionObstacle.deleteMany(query2)      
        await collectionFPerson.deleteMany(query2)
        res.status(200).json(result);
    } catch(error){
        res.status(500).json(error);
        console.log(error);
    }
}

const sendFPerson = async(req, res) => {
    try{
        const faintedAlert = {
            zoneId: req.body.zoneId,
            floor: req.body.floor,
            roomId: req.body.roomId,
            isFinished: req.body.isFinished
        }
        
    
        const db = client.db('GazDetector');
        const collection = db.collection('FaintedPerson');
        let result = await collection.insertOne(faintedAlert);
        res.status(200).json(result);
    } catch(error){
        console.log(error);
        res.status(500).json(error);
    }
}

const sendObstacle = async(req, res) => {
    try{
        const obstacle = {
            zoneId: req.body.zoneId,
            floor: req.body.floor,
            roomId: req.body.roomId,
            obstacleLevel: req.body.obstacleLevel
        }
        
    
        const db = client.db('GazDetector');
        const collection = db.collection('Obstacle');
        let result = await collection.insertOne(obstacle);
        res.status(200).json(result);
    } catch(error){
        console.log(error);
        res.status(500).json(error);
    }
}

const getObstacles = async(req, res) => {
    try{
        const db = client.db('GazDetector');
        const collection = db.collection('Obstacle');
        var query = { zoneId: req.body.zoneId};
        const result = await collection.find(query)
        const array = await result.toArray();
        if(array.length>0){ 
            res.status(200).json({array});
        } else{
            res.status(204).json({msg:"No alerts"});
        }
    } catch(error){
        console.log(error);
    }
}

const getFPersons = async(req, res) => {
    try{
        const db = client.db('GazDetector');
        const collection = db.collection('FaintedPerson');
        var query = { zoneId: req.body.zoneId};
        const result = await collection.find(query)
        const array = await result.toArray();
        console.log(array)
        if(array.length>0){ 
            res.status(200).json({array});
        } else{
            res.status(204).json({msg:"No alerts"});
        }
    } catch(error){
        console.log(error);
    }
}


app.use('/addPerson', addPerson);
app.use('/addAlert', addAlert);
app.use('/getPerson', getPerson);
app.use('/getAlert', getAlert);
app.use('/checkCurrentAlert', checkCurrentAlert);
app.use('/finishAlert', finishAlert);
app.use('/sendFPerson', sendFPerson);
app.use('/sendObstacle', sendObstacle);
app.use('/getObstacles',getObstacles);
app.use('/getFPersons', getFPersons);




app.listen(PORT, () => {
    console.log("listening on port "+PORT);
});