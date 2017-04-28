window.onload=function(){
	load();
}

//加载节点容器列表
function load(){
	$("#node_list").empty();
	$.ajax({
		type : "GET",
		global:false,
		url : "http://" + window.location.host + "/registerTarget/load",
		success : function(data) {
			if(data != "[]"){
				var obj = eval("(" + data + ")");
				var node_info=obj[0]["targets"];
				for(var i=0;i<node_info.length;i++){
					$("#node_list").append('<tr>'+
							'<td onclick="removeTarget(\''+node_info[i]["targetId"]+'\')" style="cursor:pointer"><span class="glyphicon glyphicon-remove"></span></td>'+
							'<td>'+node_info[i]["targetId"]+'</td>'+
							'<td>'+node_info[i]["targetName"]+'</td>'+
							'<td>'+node_info[i]["targetOrg"]+'</td>'+
							'<td>'+node_info[i]["principal"]+'</td>'+
							'<td>'+node_info[i]["phone"]+'</td>'+
							'<td>'+node_info[i]["email"]+'</td>'+
							'<td>'+node_info[i]["version"]+'</td>'+
							'<td>'+node_info[i]["updateTime"]+'</td>'+
							'<td>'+node_info[i]["descript"]+'</td>'+
					'</tr>');
				}
			}
		},
        error: function(XMLHttpRequest, textStatus, errorThrown) {
        	info("获取节点容器信息失败")
        }
	});
}

//下载容器
function download(i){
	if(i==1){
		window.location="http://"+ window.location.host + "/serverInfo/download1";
	}else{
		window.location="http://"+ window.location.host + "/serverInfo/download";
	}
}

//删除容器
function removeTarget(id){
	$.ajax({
		type : "GET",
		global:false,
		url : "http://" + window.location.host + "/registerTarget/delete?targetId="+id,
		success : function(data) {
			load();
			info(data);
		}
	});
}
//提示信息
function info(message){
    var dialog=BootstrapDialog.show({
         message: message,
         title: "提示消息",
    });
    setTimeout(function(){dialog.close()},2000);
}