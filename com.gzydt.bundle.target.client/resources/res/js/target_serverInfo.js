window.onload = function() {
	loadServer();
}

function loadServer() {
	$.ajax({
		type : "GET",
		global : false,
		url : "http://" + window.location.host + "/targetInfo/getLog",
		success : function(data) {
			var obj = eval("(" + data + ")");
			var address = obj["address"];
			var log = obj["logs"];
			var num = 0;
			$("#serverAdd").text(obj["address"]);
			$("#state").text(obj["state"]);
			$("#serverName").text(obj["name"]);
			$("#principal").text(obj["principal"]);
			$("#descript").text(obj["descript"]);
			$("#phone").text(obj["phone"]);
			$("#email").text(obj["email"]);
			if ($("#state").text() == "连接失败") {
				$("#errorInfo").append("<p>服务器连接失败</p>");
				num++;
			}
			for (var i = 0; i < log.length; i++) {
				$("#errorInfo").append("<p>" + log[i] + "</p>");
				num++;
			}
			if (num > 0) {
				$("#errorInfo").show();
			} else {
				$("#errorInfo").hide();
			}
			$("#errorNum").text(num);
		}
	});

}
