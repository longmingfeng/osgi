window.onload=function(){
	showSystemList();
	selectSystem(1,"0");
	getLogNum();
}

var currendId;

function showSystemList(){
	$.ajax({
        type:"GET",
        global:false, 
        url:"http://"+window.location.host+"/targetInfo/getSystemInfo",
        success:function (data) {
           var obj = eval("(" + data + ")");
           for (var i = 0; i < obj.length; i++) {
        	   $("#systemList").append("<li class='list-group-item' id='"+i+"' onclick='selectSystem(1,\""+obj[i]["id"]+"\",\""+i+"\")'><span class='badge'>"+obj[i]["version"]+"</span>"+obj[i]["name"]+"</li>");
		   }
        }
	});
}

//获取服务器信息日志数目
function getLogNum(){
	$.ajax({
        type:"GET",
        global:false, 
        url:"http://"+window.location.host+"/targetInfo/getLogNum",
        success:function (data) {
        	if(data!="{}"){
        		var obj = eval("(" + data + ")");
        		var num=obj["logs"].length;
        		if(obj["state"]=="连接失败"){
        			num++;
        		}
        		$("#errorNum").text(num);
        	}
        }
    }); 
}

//查询指定条件组件
function searchBundle(page,id){
	if($("#e_startTime").val()>$("#e_endTime").val()&&$("#e_endTime").val()!=null&&$("#e_endTime").val()!=""){
		info("输入的结束时间必须大于开始时间");
		return false;
	}
	//GO转跳
	if(page==undefined){
		var goValue = $('#goValu').val();
		var reNum = /^\d*$/;
		//输入的不是数字或者为空
		if(!reNum.test(goValue) || goValue==""){
			return false;
		}
		page = $('#goValu').val();
	}
	$("#bundle_list").empty();
	$.ajax({
        type:"GET",
        global:false, 
        url:"http://"+window.location.host+"/findTarget?bundlename="+$("#e_bundleName").val()+"&bundlestate="+$("#e_state").val()+
             "&is_systemBundle="+$("#e_type").val()+"&bundle_installTime_start="+$("#e_startTime").val()+"&bundle_installTime_end="+
             $("#e_endTime").val()+"&id="+id,
        success:function (data) {
	            var obj = eval("(" + data + ")");
	        	var bundle_info=obj["bundleinfo"];
	        if(bundle_info.length>0){
	        	var num = (page*10)>bundle_info.length?bundle_info.length:(page*10);
	        	debugger;
	        	for(var i=(page-1)*10+1;i<=num;i++){
	        		var icon='<img src="'+pluginRoot+'/res/image/系统类型.png" title="系统组件"/>';
	        		if(bundle_info[i-1]["is_systemBundle"]!="true"){
	        			icon='<img src="'+pluginRoot+'/res/image/安装类型.png" title="安装组件"/>';
	        		}
	        		$("#bundle_list").append('<tr>'+
	        				'<td>'+i+'</td>'+
	        				'<td>'+icon+'</td>'+
	        				'<td>'+bundle_info[i-1]["bundlename"]+'</td>'+
	        				'<td><img src="'+pluginRoot+'/res/image/'+bundle_info[i-1]["bundlestate"]+'状态.png" title="'+bundle_info[i-1]["bundlestate"]+'"/></td>'+
	        				'<td>'+bundle_info[i-1]["bundleversion"]+'</td>'+
	        				'<td>'+(bundle_info[i-1]["descrip"]==null?"没有相关描述":bundle_info[i-1]["descrip"])+'</td>'+
	        				'<td>'+bundle_info[i-1]["bundle_installTime"]+'</td>'+
	        			'</tr>');
	        	}
	        	$("#bundleTotalNum").text(bundle_info.length);
	        	createNav(page,bundle_info.length,10,"searchBundle",id);
        	}else{
        		$("#bundleTotalNum").text(bundle_info.length);
        		$("#bundle_list").append("<tr><td colspan='7'>暂无组件</td></tr>");
        	}
        }
    });
}

function selectSystem(page,id){
	//GO转跳
	if(page==undefined){
		var goValue = $('#goValu').val();
		var reNum = /^\d*$/;
		//输入的不是数字或者为空
		if(!reNum.test(goValue) || goValue==""){
			return false;
		}
		page = $('#goValu').val();
	}
	$("#bundle_list").empty();
	$(".list-group-item").css("background-color","#ffffff");
	$("#"+id).css("background-color","#D9EDF7");
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/findTarget?id="+id,
        global:false, 
        success:function (data) {
            var obj = eval("(" + data + ")");
        	var bundle_info=obj["bundleinfo"];
        if(bundle_info.length>0){
        	var num = (page*10)>bundle_info.length?bundle_info.length:(page*10);
        	for(var i=(page-1)*10+1;i<=num;i++){
        		var icon='<img src="'+pluginRoot+'/res/image/系统类型.png" title="系统组件"/>';
        		if(bundle_info[i-1]["is_systemBundle"]!="true"){
        			icon='<img src="'+pluginRoot+'/res/image/安装类型.png" title="安装组件"/>';
        		}
        		$("#bundle_list").append('<tr>'+
        				'<td>'+i+'</td>'+
        				'<td>'+icon+'</td>'+
        				'<td>'+bundle_info[i-1]["bundlename"]+'</td>'+
        				'<td><img src="'+pluginRoot+'/res/image/'+bundle_info[i-1]["bundlestate"]+'状态.png" title="'+bundle_info[i-1]["bundlestate"]+'"/></td>'+
        				'<td>'+bundle_info[i-1]["bundleversion"]+'</td>'+
        				'<td>'+(bundle_info[i-1]["descrip"]==null?"没有相关描述":bundle_info[i-1]["descrip"])+'</td>'+
        				'<td>'+bundle_info[i-1]["bundle_installTime"]+'</td>'+
        			'</tr>');
        	}
        	$("#bundleTotalNum").text(bundle_info.length);
        	$("#search").attr("onclick","searchBundle(1,"+id+")");
        	currendId=id;
        	createNav(page,bundle_info.length,10,"selectSystem",id);
          }else{
      		$("#bundleTotalNum").text(bundle_info.length);
      		$("#search").attr("onclick","searchBundle(1,"+id+")");
    		$("#bundle_list").append("<tr><td colspan='7'>暂无组件</td></tr>");
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

function keySearch(){
	if(window.event.keyCode == 13){
		searchBundle(1,currendId);
	}
}


