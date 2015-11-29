require('epipebomb')()
var restify = require('restify');
var server4socket = require("http").createServer().listen(8080);
var io = require("socket.io").listen(server4socket);

var sumo = require('jumping-night-drone');
var drone = sumo.createClient();

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
  res.send('emergency');

  var server4socket2Client = require("http").createServer().listen(8082);
  var io2C = require("socket.io").listen(server4socket2Client);

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
