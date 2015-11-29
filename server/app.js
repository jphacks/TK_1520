var restify = require('restify');
var server4socket = require("http").createServer().listen(8080);
var io = require("socket.io").listen(server4socket);

var sumo = require('jumping-night-drone');
var drone = sumo.createClient();

// Twilio Credentials 
var accountSid = process.env.SID; 
var authToken = process.env.TOKEN; 

drone.connect(function() {
  //console.log("connect!");

  var video = drone.getVideoStream();
  video.on("data", function(data) {
    process.stdout.write(data);
  });

  // socket wit socket.io 8080---------------------------------------------------
  io.sockets.on("connection", function (socket) {
    // ジャイロを受け取る
    socket.on("gyro", function (data) {
      var obj = JSON.parse(data);
      var x = obj.dir_x;
      var y = obj.dir_y;
      //console.log(x + ' '+ y);

      if(y < 0){
        drone.stop();
        return;
      }

      if(x < 0){
        drone.curveForwardRight(30, -x*0.3);
      }else{
        drone.curveForwardLeft(30, x*0.3);
      }
    });
  });
  //------------------------------------------------------------------------------

});


// API server with restify 8081------------------------------------------------
function hello(req, res, next) {
  res.send('hello ' + req.params.name + ' ' + req.params.add);
}

function move(req, res, next) {
  res.send('move ' + req.params.x + ' ' + req.params.y + ' ' + req.params.z);
}

function emergency(req, res, next) {
    var client = require('twilio')(accountSid, authToken); 
    client.calls.create({
        //FIXME : Hard-coded request
	to: "+819066223991", 
	from: "+819066223991", 
	url: "http://hamanishi.orz.hm/twillio/twillio.xml",  
	method: "GET",  
	fallbackMethod: "GET",  
	statusCallbackMethod: "GET",    
	record: "false" 
    }, function(err, call) { 
	console.log(call.sid); 
    });
    res.send('emergency');
}

var server4api = restify.createServer();
server4api.use(restify.queryParser());

server4api.get('/hello', hello);
server4api.get('/move', move);
server4api.get('/emergency', emergency);

server4api.listen(8081, function() {
  //console.log('%s listening at %s', server4api.name, server4api.url);
});
//-----------------------------------------------------------------------------
