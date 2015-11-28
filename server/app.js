var restify = require('restify');
var server4socket = require("http").createServer().listen(8080);
var io = require("socket.io").listen(server4socket);

// API server with restify 8081------------------------------------------------
function hello(req, res, next) {
  res.send('hello ' + req.params.name + ' ' + req.params.add);
}

function move(req, res, next) {
  res.send('move ' + req.params.x + ' ' + req.params.y + ' ' + req.params.z);
}

function emergency(req, res, next) {
  res.send('emergency');
}

var server4api = restify.createServer();
server4api.use(restify.queryParser());

server4api.get('/hello', hello);
server4api.get('/move', move);
server4api.get('/emergency', emergency);

server4api.listen(8081, function() {
  console.log('%s listening at %s', server4api.name, server4api.url);
});
//-----------------------------------------------------------------------------


// socket wit socket.io 8080---------------------------------------------------
io.sockets.on("connection", function (socket) {

  // 接続開始カスタムイベント(接続元ユーザを保存し、他ユーザへ通知)
  socket.on("connected", function () {
    var msg = "connected!";
    io.sockets.emit("publish", {value: msg});
  });

  // メッセージ送信カスタムイベント
  socket.on("publish", function (data) {
    io.sockets.emit("publish", {value:data.value});
  });

  // 接続終了組み込みイベント(接続元ユーザを削除し、他ユーザへ通知)
  socket.on("disconnect", function () {
    var msg = "disconnected!";
    io.sockets.emit("publish", {value: msg});
  });
});
//------------------------------------------------------------------------------
