//TODO(NatsukiHamanishi): This is a quick fix. ACCESS shoud be determined dynamically.
var ACCESS ="http://192.51.208.62:8080";
//var ACCESS ="http://192.168.1.4:????;

$(loaded);
function loaded(){

	//    var socket_cotroller = io.connect(ACCESS + "/connection");
	var socket_cotroller = io.connect(ACCESS);

	$("#title").append("<h1>奴隷</h1>");

	window.addEventListener("devicemotion", function(event1){
		var x = event1.accelerationIncludingGravity.x;
		var y = event1.accelerationIncludingGravity.y;
		var z = event1.accelerationIncludingGravity.z;
	}, true);

	window.addEventListener('deviceorientation', function(event2) {
		var beta = event2.beta; //x
		var gamma = event2.gamma; //y
		var alpha = event2.alpha; //z

		dir_x = gamma;
		dir_y = beta;

		var json = JSON.stringify({dir_x:dir_x.toString(), dir_y:dir_y.toString()});
		socket_cotroller.emit('gyro', json);

	}, true);

	var vertical_canvas = document.getElementById("vertical_canvas");
	var horizontal_canvas = document.getElementById("horizontal_canvas");

	var vertical_context = vertical_canvas.getContext("2d");
	var vertical_canvas_width = vertical_canvas.width;
	var vertical_canvas_height = vertical_canvas.height;

	var horizontal_context = horizontal_canvas.getContext("2d");
	var horizontal_canvas_width = horizontal_canvas.width;
	var horizontal_canvas_height = horizontal_canvas.height;

	var vertical_ball_x = vertical_canvas.width / 2
	var vertical_ball_y = vertical_canvas.height / 2;
	var horizontal_ball_x = horizontal_canvas.width / 2
	var horizontal_ball_y = horizontal_canvas.height / 2;

	var dir_x = 0
	var dir_y = 0;

	var vertical_block_array = new Array;
	vertical_block_array[0] = new Array;
	vertical_block_array[1] = new Array;

	var horizontal_block_array = new Array;
	horizontal_block_array[0] = new Array;
	horizontal_block_array[1] = new Array;

	var vertical_block_height = vertical_canvas.height/10;
	var horizontal_block_height = horizontal_canvas.height/10;
	var vertical_block_width = vertical_canvas.width/10;
	var horizontal_block_width = horizontal_canvas.width/10;

	var goal_startx = 0;
	var goal_endx = 20;
	var goal_starty = 0;
	var goal_endy = 20;

	var interval=10;

	setInterval(drawBall, interval);
	setInterval(checkGoal, interval);

	function drawBall() {

		initCanvas();

		//set background color
		vertical_context.beginPath();
		vertical_context.fillStyle = 'rgb(0,255,0)';
        vertical_context.fillRect(0, 0, vertical_canvas_width, vertical_canvas_height);

		horizontal_context.beginPath();
		horizontal_context.fillStyle = 'rgb(0,0,255)';
        horizontal_context.fillRect(0, 0, horizontal_canvas_width, horizontal_canvas_height);

		//draw balls
		vertical_context.beginPath();
		vertical_context.arc(vertical_ball_x, vertical_ball_y, 50, 0, 2*Math.PI, true);
		vertical_context.fillStyle="rgb(0,0,0)";
		vertical_context.fill();

		horizontal_context.beginPath();
		horizontal_context.arc(horizontal_ball_x, horizontal_ball_y, 50, 0, 2*Math.PI, true);
		horizontal_context.fillStyle="rgb(0,0,0)";
		horizontal_context.fill();

		//check collision
		var vtx = vertical_ball_x  + dir_x;
		var vty = vertical_ball_y  + dir_y;
		if (0 < vtx && vtx < vertical_canvas_width ) { vertical_ball_x = vtx; }
		if (0 < vty && vty < vertical_canvas_height) { vertical_ball_y = vty; }

		var tx = horizontal_ball_x + dir_x;
		var ty = horizontal_ball_y + dir_y;
		if (0 < tx && tx < horizontal_canvas_width ) { horizontal_ball_x = tx; }
		if (0 < ty && ty < horizontal_canvas_height) { horizontal_ball_y = ty; }


		$("#ball_position_vertical").empty();
		$("#ball_position_horizontal").empty();
		$("#ball_position_vertical").append("x:" + vertical_ball_x + ",y:" + vertical_ball_y );
		$("#ball_position_horizontal").append("x:" + horizontal_ball_x + ",y:" + horizontal_ball_y );

	}

	function initCanvas() {

		vertical_context.clearRect(0, 0, vertical_canvas.width, vertical_canvas.height);
		vertical_context.fillStyle = "rgb(200, 0, 0)";
		vertical_context.fillRect(goal_startx, goal_starty, goal_endx, goal_endy);

		horizontal_context.clearRect(0, 0, horizontal_canvas.width, horizontal_canvas.height);
		horizontal_context.fillStyle = "rgb(200, 0, 0)";
		horizontal_context.fillRect(goal_startx, goal_starty, goal_endx, goal_endy);

	}

	function checkGoal() {
		$("#message").empty();
		if (goal_startx <= vertical_ball_x && vertical_ball_x <= goal_endy &&
			goal_starty <= vertical_ball_y && vertical_ball_y <= goal_endy) {
				$("#message").append("ごーるです！");
		}
	}

}
