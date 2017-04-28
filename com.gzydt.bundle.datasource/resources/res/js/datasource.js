window.onload=function(){
	loadOption();
	loadDS();
}

function loadOption(){
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/getDriverInfo/getInfo",
        success:function (data) {
        	var array = eval("(" + data + ")");
        	var obj=[]
            for(var i = 0; i < array.length; i++){
               if (obj.indexOf(array[i]["driverName"]) == -1)
        		   obj.push(array[i]);
        	}
        	for (var i = 1; i <= obj.length; i++) {
        		var option="";
        		if(obj[i-1]["driverName"].indexOf("oracle")!=-1){
        			option="oracle";
        		}else if(obj[i-1]["driverName"].indexOf("mysql")!=-1){
        			option="mysql"
        		}else if(obj[i-1]["driverName"].indexOf("h2")!=-1){
        			option="h2"
        		}
        		if(option!=""){
        			$("#type").append('<option value="'+obj[i-1]["driverName"]+'">'+option+'</option>');
        		}
			}
        }
    }); 	
}

function initDs(){
	$("#testConn").show();
	$("#saveDs").show();
	$("#dataSourceH").attr("class", "glyphicon glyphicon-minus");
	$("#dataSourceCont").show();
	$("#poolH").attr("class", "glyphicon glyphicon-plus");
	$("#poolCont").hide();
	$("#dsname").val("testDS");
	$("#jdbcIp").val("localhost");
	$("#jdbcPort").val("3306");
	$("#jdbcDBName").val("test");
	$("#username").val("test");
	$("#password").val("123");
	$("#context").val("请填写描述信息。");
	$("#select_type").show();
	$("#input_type").hide();
	$("#radio_jta").show();
	$("#input_jta").hide();
	$("#radio_pooltype").show();
	$("#input_pooltype").hide();
	$("#initNum").val("20");
	$("#maxNum").val("100");
	$("#maxActive").val("300");
	$("#minIdle").val("300");
	$("#maxIdle").val("1000");
	$("#maxWait").val("1000");
	$("#timeBetweenEvictionRunsMillis").val("60000");
	$("#minEvictableIdleTimeMillis").val("300000");
	$("#connectionTimeout").val("30000");
	$("#idleTimeout").val("600000");
	$("#maxLifetime").val("180000");
}

function testConn(){
	var ip=$("#jdbcIp").val();
	var port=$("#jdbcPort").val();
	var dbName=$("#jdbcDBName").val();
	var user=$("#username").val();
	var password=$("#password").val();
	var type=$("#type").val();
	var url;
	if(type.indexOf("oracle")!=-1){
		url="jdbc:oracle:thin:@"+ip+":"+port+":"+dbName;
	}else if(type.indexOf("mysql")!=-1){
		url="jdbc:mysql://"+ip+":"+port+"/"+dbName;
	}else if(type.indexOf("h2")!=-1){
		url="jdbc:h2:mem:"+dbName;
	}
	if(type.indexOf("oracle")!=-1){
		type="oracle";
	}else if(type.indexOf("mysql")!=-1){
		type="mysql"
	}else if(obj[i-1]["driverName"].indexOf("h2")!=-1){
		option="h2"
	}
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/getDriverInfo/getConn?url="+url+"&user="+user+"&password="+password+"&type="+type,
        success:function (data) {
        	if(data=="true"){
        		info("连接成功");
        	}else{
        		info("连接失败");
        	}
        }
    }); 
}

function saveDS(){
	var ip=$("#jdbcIp").val();
	var port=$("#jdbcPort").val();
	var dbName=$("#jdbcDBName").val();
	var user=$("#username").val();
	var password=$("#password").val();
	var type=$("#type").val();
	var dsname=$("#dsname").val();
	var managed=$("input[name='jta']:checked").val();
	var context=$("#context").val();
	var initNum=$("#initNum").val();
	var maxNum=$("#maxNum").val();
	var maxIdle=$("#maxIdle").val();
	var minIdle=$("#minIdle").val();
	var pooltype=$("input[name='pooltype']:checked").val();
	var maxActive=$("#maxActive").val();
	var maxWait=$("#maxWait").val();
	var timeBetweenEvictionRunsMillis=$("#timeBetweenEvictionRunsMillis").val();
	var minEvictableIdleTimeMillis=$("#minEvictableIdleTimeMillis").val();
	var connectionTimeout=$("#connectionTimeout").val();
	var idleTimeout=$("#idleTimeout").val();
	var maxLifetime=$("#maxLifetime").val();
	var url;
	if(type.indexOf("oracle")!=-1){
		url="jdbc:oracle:thin:@"+ip+":"+port+":"+dbName;
	}else if(type.indexOf("mysql")!=-1){
		url="jdbc:mysql://"+ip+":"+port+"/"+dbName;
	}else if(type.indexOf("h2")!=-1){
		url="jdbc:h2:mem:"+dbName;
	}
	$.ajax({
        type:"POST",
        url:"http://"+window.location.host+"/getDriverInfo/save",
        data:"url="+url+"&user="+user+"&password="+password+"&type="+type+"&dsname="+dsname+"&jta="+managed+"&context="+context
        +"&initNum="+initNum+"&maxNum="+maxNum+"&maxIdle="+maxIdle+"&minIdle="+minIdle+"&pooltype="+pooltype
        +"&maxActive="+maxActive+"&maxWait="+maxWait+"&timeBetweenEvictionRunsMillis="+timeBetweenEvictionRunsMillis
        +"&minEvictableIdleTimeMillis="+minEvictableIdleTimeMillis+"&connectionTimeout="+connectionTimeout
        +"&idleTimeout="+idleTimeout+"&maxLifetime="+maxLifetime,
        success:function (data) {
        	if(data=="true"){
        		info("保存成功");
        		loadDS();
        	}else{
        		info("保存失败");
        	}
        }
    }); 
}

function showPoolProp(){
	var pooltype=$("input[name='pooltype']:checked").val();
	if(pooltype=="hikari"){
		$("#div_timeBetweenEvictionRunsMillis").show();
		$("#div_minEvictableIdleTimeMillis").show();
		$("#div_connectionTimeout").show();
		$("#div_idleTimeout").show();
		$("#div_maxLifetime").show();
	}
	else if(pooltype=="druid"){
		$("#div_timeBetweenEvictionRunsMillis").hide();
		$("#div_minEvictableIdleTimeMillis").hide();
		$("#div_connectionTimeout").hide();
		$("#div_idleTimeout").hide();
		$("#div_maxLifetime").hide();
	}
	else {
		$("#div_timeBetweenEvictionRunsMillis").hide();
		$("#div_minEvictableIdleTimeMillis").hide();
		$("#div_connectionTimeout").hide();
		$("#div_idleTimeout").hide();
		$("#div_maxLifetime").hide();
	}
}

function hiddenDataSource(){
	if($("#dataSourceH").attr("class")=="glyphicon glyphicon-minus"){
		$("#dataSourceH").attr("class", "glyphicon glyphicon-plus");
		$("#dataSourceCont").hide();
		$("#poolH").attr("class", "glyphicon glyphicon-minus");
		$("#poolCont").show();
	}else{
		$("#dataSourceH").attr("class", "glyphicon glyphicon-minus");
		$("#dataSourceCont").show();
		$("#poolH").attr("class", "glyphicon glyphicon-plus");
		$("#poolCont").hide();
	}
}
function hiddenPool(){
	if($("#poolH").attr("class")=="glyphicon glyphicon-minus"){
		$("#poolH").attr("class", "glyphicon glyphicon-plus");
		$("#poolCont").hide();
		$("#dataSourceH").attr("class", "glyphicon glyphicon-minus");
		$("#dataSourceCont").show();
	}else{
		$("#poolH").attr("class", "glyphicon glyphicon-minus");
		$("#poolCont").show();
		$("#dataSourceH").attr("class", "glyphicon glyphicon-plus");
		$("#dataSourceCont").hide();
	}
}

function loadDS(){
	$("#datasource_list").empty();
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/getDriverInfo/getDS",
        success:function (data) {
        	var obj = eval("(" + data + ")");
        	for (var i = 1; i <= obj.length; i++) {
        		var message="";
        		if(obj[i-1]["message"]!="无"){
        			message='<td>'+obj[i-1]["state"]+'<img src="'+pluginRoot+'/res/image/warning.png"  onclick="info(\''+obj[i-1]["message"]+'\')"/></td>';
        		}else{
        			message='<td>'+obj[i-1]["state"]+'</td>';
        		}
        		var disc=obj[i-1]["discription"]==null?"":obj[i-1]["discription"];
        		var pool=obj[i-1]["pool"]==null?"":obj[i-1]["pool"];
				$("#datasource_list").append('<tr>'+
        				'<td>'+i+'</td>'+
        				'<td><a onclick="showDS(\''+(i-1)+'\')" data-toggle="modal" data-target="#dataSourceModal">'+obj[i-1]["name"]+'</a></td>'+
        				'<td>'+disc+'</td>'+
        				'<td>'+obj[i-1]["driverClassName"]+'</td>'+
        				message+
        				'<td>'+pool+'</td>'+
        			'</tr>');
			}
        }
    }); 
}

function showDS(id){
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/getDriverInfo/getDS",
        success:function (data) {
        	var obj = eval("(" + data + ")");
        	$("#testConn").hide();
        	$("#saveDs").hide();
        	$("#dataSourceH").attr("class", "glyphicon glyphicon-minus");
            $("#dataSourceCont").show();
            $("#poolH").attr("class", "glyphicon glyphicon-plus");
        	$("#poolCont").hide();
            $("#dsname").val(obj[id].name);
            var url=obj[id].jdbcUrl;
            if(obj[id].driverClassName.indexOf("oracle")!=-1){
            	var urlStrings=url.split(':');
            	$("#jdbcIp").val(urlStrings[3].substring(1));
                $("#jdbcPort").val(urlStrings[4]);
                $("#jdbcDBName").val(urlStrings[5]);
        	}else if(obj[id].driverClassName.indexOf("mysql")!=-1){
        		var urlStrings=url.split('/');
        		$("#jdbcIp").val(urlStrings[2].split(":")[0]);
                $("#jdbcPort").val(urlStrings[2].split(":")[1]);
                $("#jdbcDBName").val(urlStrings[3]);
        	}else if(obj[id].driverClassName.indexOf("h2")!=-1){
        		$("#jdbcIp").val("localhost");
                $("#jdbcPort").val("无");
                $("#jdbcDBName").val(url.substring(url.lastIndex(":")+1));
        	}
            $("#username").val(obj[id].username);
            $("#password").val("*************");
            $("#context").val(obj[id].discription);
            
            $("#select_type").hide();
            $("#input_type").show();
            var option="";
            if(obj[id].driverClassName.indexOf("oracle")!=-1){
    			option="oracle";
    		}else if(obj[id].driverClassName.indexOf("mysql")!=-1){
    			option="mysql"
    		}else if(obj[id].driverClassName.indexOf("h2")!=-1){
    			option="h2"
    		}
            $("#type_input").val(option);
            $("#radio_jta").hide();
            $("#input_jta").show();
            $("#jta_input").val(obj[id].managed);
            $("#radio_pooltype").hide();
            $("#input_pooltype").show();
            $("#pooltype_input").val(obj[id].pool);
            $("#initNum").val(obj[id].initialSize);
        	$("#maxNum").val(obj[id].maximumPoolSize);
        	$("#maxActive").val(obj[id].maxActive);
        	$("#minIdle").val(obj[id].minIdle);
        	$("#maxIdle").val(obj[id].maxIdle);
        	$("#maxWait").val(obj[id].maxWait);
        	$("#timeBetweenEvictionRunsMillis").val(obj[id].timeBetweenEvictionRunsMillis);
        	$("#minEvictableIdleTimeMillis").val(obj[id].minEvictableIdleTimeMillis);
        	$("#connectionTimeout").val(obj[id].connectionTimeout);
        	$("#idleTimeout").val(obj[id].idleTimeout);
        	$("#maxLifetime").val(obj[id].maxLifetime);
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