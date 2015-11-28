var restify = require('restify');

function hello(req, res, next) {
  res.send('hello ' + req.params.name + ' ' + req.params.add);
}

function move(req, res, next) {
  res.send('move ' + req.params.x + ' ' + req.params.y + ' ' + req.params.z);
}

function emergency(req, res, next) {
  res.send('emergency');
}


var server = restify.createServer();
server.use(restify.queryParser());

server.get('/hello', hello);
server.get('/move', move);
server.get('/emergency', emergency);
//server.head('/hello/:name', hello);

server.listen(8080, function() {
  console.log('%s listening at %s', server.name, server.url);
});
