window.onload=function(){
	loadTargetInfo();
	loadProp();
	getLogNum();
}

//加载target基本信息
function loadTargetInfo(){
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/targetInfo/info",
        success:function (data) {
            var obj = eval("(" + data + ")");
            $("#targetName").text(obj["targetName"]);
            $("#targetOrg").text(obj["targetOrg"]);
            $("#principal").text(obj["principal"]);
            $("#phone").text(obj["phone"]);
            $("#email").text(obj["email"]);
            $("#descript").text(obj["descript"]);
            $("#version").text(obj["version"]);
            $("#updateTime").text(obj["updateTime"]);
        }
    }); 	
}
//加载taget配置信息
function loadProp(){
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/targetInfo/prop",
        success:function (data) {
            var obj = eval("(" + data + ")");
            $("#log_level").text(obj["agent.logging.level"]);
            $("#syncinterval").text(obj["agent.controller.syncinterval"]);
            $("#syncdelay").text(obj["agent.controller.syncdelay"]);
            $("#streaming").text(obj["agent.controller.streaming"]);
            $("#retries").text(obj["agent.controller.retries"]);
            $("#fixpackages").text(obj["agent.controller.fixpackages"]);
        }
    }); 
}
//获取服务器信息日志数目
function getLogNum(){
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/targetInfo/getLogNum",
        success:function (data) {
        	if(data!="{}"){
        		var obj = eval("(" + data + ")");
        		var log=obj["logs"];
        		var num=0;
        		if(obj["state"]=="连接失败"){
        			num++;
        		}
        		for (var i = 0; i < log.length; i++) {
        			num++;
        		}
        		$("#errorNum").text(num);
        	}
        }
    }); 
}
//获取当前的target的基本信息进到修改页面
function changeInfo(){
	$("#editTargetName").val($("#targetName").text());
	$("#editTargetOrg").val($("#targetOrg").text());
	$("#editPrincipal").val($("#principal").text());
	$("#editPhone").val($("#phone").text());
	$("#editEmail").val($("#email").text());
	$("#editDescript").val($("#descript").text());
	$("#editVersion").val($("#version").text());
	$("#editUpdateTime").val($("#updateTime").text());
	
}
//获取当前的target的配置信息进到修改页面
function changeProp(){
	$("#edit_log_level").val($("#log_level").text());
	$("#edit_syncinterval").val($("#syncinterval").text());
	$("#edit_syncdelay").val($("#syncdelay").text());
	$("#edit_streaming").val($("#streaming").text());
	$("#edit_retries").val($("#retries").text());
	$("#edit_fixpackages").val($("#fixpackages").text());
}

//提交修改的target基本信息
function submitTargetInfo(){
	if($("#editTargetName").val()==""||$("#editTargetOrg").val()==""||$("#editPrincipal").val()==""||$("#editPhone").val()==""){
		info("必填项不能为空");
		return false;
	}
	$.ajax({
        type:"POST",
        url:"http://"+window.location.host+"/targetInfo/info",
        data:"targetName="+$("#editTargetName").val()+"&targetOrg="+$("#editTargetOrg").val()+"&principal="+$("#editPrincipal").val()+"&phone="+$("#editPhone").val()+"&email="+$("#editEmail").val()+"&descript="+$("#editDescript").val(),
        success:function (data) {
            if(data=="true"){
            	loadTargetInfo();
            	info("修改成功");
            	$('#target_info').modal('hide');
            }else{
            	info("修改失败");
            }
        }
    }); 
}
//提交修改的target配置信息
function submitTargetProp(){
	$.ajax({
        type:"POST",
        url:"http://"+window.location.host+"/targetInfo/prop",
        data:"log_level="+$("#edit_log_level").val()+"&syncinterval="+$("#edit_syncinterval").val()+"&syncdelay="+$("#edit_syncdelay").val()+"&streaming="+$("#edit_streaming").val()+"&fixpackages="+$("#edit_fixpackages").val()+"&retries="+$("#edit_retries").val(),
        success:function (data) {
            if(data=="true"){
            	loadProp();
            	info("修改成功");
            }else{
            	info("修改失败");
            }
        }
    }); 
}

function info(message){
    var dialog=BootstrapDialog.show({
         message: message,
         title: "提示消息",
    });
    setTimeout(function(){dialog.close()},2000);
}


