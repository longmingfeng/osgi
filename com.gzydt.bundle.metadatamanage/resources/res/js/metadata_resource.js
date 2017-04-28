//提示信息
function info(message){
    var dialog=BootstrapDialog.show({
         message: message,
         title: "提示消息",
    });
    setTimeout(function(){dialog.close()},2000);
}

//加载方法
window.onload=function(){
	load(1,10);
	$("#go").click(function(){
		var goValue = $('#goValu').val();
		var reNum = /^\d*$/;
		//输入的不是数字或者为空
		if(!reNum.test(goValue) || goValue==""){
			goValue=1;
		}
		load(goValue,10)
	});
}

//初始化元数据
function ininMetaDate(){
	$("#metadataId").val("");
	$("#metadataId").removeAttr("disabled");
    $("#metadataName").val("");
    $("#metadataType").val("");
    $("#metadataauthor").val("");
    $("#metadataState").val("1");
    $("#metadataDesc").val("");
    $("#metadataRType").val("0");
    $("#metadataRPath").val("");
    $("#metadataRPath").empty();
    $.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/getDriverInfo/getDS",
        async: false,
        success:function (data) {
        	var obj = eval("(" + data + ")");
        	for (var i = 1; i <= obj.length; i++) {
        		$("#metadataRPath").append("<option value='"+obj[i-1]["name"]+"'>"+obj[i-1]["name"]+"</option>");
			}
        }
    });
}

//加载元数据
function load(pageNum,pageSize){
	$("#base_list").empty();
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/mateDataManager/getRMAll?pageNum="
             +pageNum+"&pageSize="+pageSize+"&isStop="+-1+"&keyword=",
        success:function (data) {
        	var result=eval("(" + data + ")");
        	metadatas=result["data"];
        	for (var i = 0; i < metadatas.length; i++) {
        		var state=metadatas[i]["state"]=="1"?"启用":"停用";
        		var opState=metadatas[i]["state"]=="1"?"停用":"启用";
        		var resourceType=metadatas[i]["resourceType"];
        		var resourceValue="";
        		if(resourceType=="1"){
        			resourceValue="文件";
        		}else if(resourceType=="2"){
        			resourceValue="内存";
        		}else{
        			resourceValue="数据库";
        		}
        		var opStateVal=metadatas[i]["state"]=="1"?"0":"1";
        		$("#base_list").append("<tr>" +
        				                     "<td class='active'>"+metadatas[i]["id"]+"</td>"+
        				                     "<td class='success'>"+metadatas[i]["name"]+"</td>"+
        				                     "<td class='warning'>"+metadatas[i]["type"]+"</td>"+
        				                     "<td class='danger'>"+metadatas[i]["author"]+"</td>"+
        				                     "<td class='info'>"+resourceValue+"</td>"+
        				                     "<td class='active'>"+state+"</td>"+
        				                     "<td class='success'>"+metadatas[i]["content"]+"</td>"+
        				                     "<td class='warning'><a href='#' onclick='getMetaData(\""+metadatas[i]["id"]+"\")' data-toggle='modal' data-target='#baseMeta_Info'>修改</a>" +
        				                     "|<a href='#' onclick='changeState(\""+metadatas[i]["id"]+"\", \""+opStateVal+"\")'>"+opState+"</a>" +
        				                     "|<a href='#' onclick='removes(\""+metadatas[i]["id"]+"\")'>删除</a></td>"+
        									 "</tr>");
			}
        	pageNum=parseInt(pageNum); 
        	var pageCount=parseInt(result["pageCount"]);
        	if(pageNum>=pageCount){
        		pageNum=pageCount;
        	}
        	$("#pageUL").empty();
        	var pagehtml = '<li style="float:left;font-size: 12px;height:35px; line-height:35px;margin-right:20px">'+
			   '<div style="font-size:14px">共：'+pageCount+'&nbsp;页</div>'+
			   '</li>';
        	if(pageNum>1){
        		pagehtml+= '<li><a href="#" onclick="load(\''+(pageNum-1)+'\',\''+10+'\')">上一页</a></li>';
        	}
        	for(var i=0;i<pageCount;i++){
        		if(i>=(pageNum-3) && i<(pageNum+3)){
        			if(i==pageNum-1){
        				pagehtml+= '<li class="active"><a href="#" onclick="load(\''+(i+1)+'\',\''+10+'\')">'+(i+1)+'</a></li>';
        			}else{
        				pagehtml+= '<li><a href="#" onclick="load(\''+(i+1)+'\',\''+10+'\')">'+(i+1)+'</a></li>';
        			}
        		}
        	}
        	if(pageNum<pageCount){
        		pagehtml+= '<li><a href="#" onclick="load(\''+(pageNum+1)+'\',\''+10+'\')">下一页</a></li>';
        	}
        	$("#pageUL").append(pagehtml);
        }
    }); 
}

//保存或者修改元数据
function saveOrUpdate(){
	var validateResult = $('#resourceMetaDataForm').validate({
		errorPlacement:function(error,element) {
			//设置错误提示信息显示位置
			if(element[0].name == "metadataId"){
				$("#emsg").show();
				$("#emsg").html(error);
			}else if(element[0].name == "metadataName"){
				$("#emsg").show();
				$("#emsg").html(error);
			}else if(element[0].name == "metadataType"){
				$("#emsg").show();
				$("#emsg").html(error);
			}else if(element[0].name == "metadataRPath"){
				$("#emsg").show();
				$("#emsg").html(error);
			}else{
				$("#emsg").hide();
			}
	   }
	}).form();
	if(!validateResult){
		//校验不通过
		return false;
	}
	$.ajax({
        type:"POST",
        url:"http://"+window.location.host+"/mateDataManager/saveOrUpdateR",
        data:"id="+$("#metadataId").val()+"&name="+$("#metadataName").val()+"&type="+$("#metadataType").val()
             +"&author="+$("#metadataauthor").val()+"&state="+$("#metadataState").val()+"&desc="+$("#metadataDesc").val()+
             "&rType="+$("#metadataRType").val()+"&path="+$("#metadataRPath").val(),
        success:function (data) {
        	$('#baseMeta_Info').modal('hide');
        	load(1,10);
        	info(data);
        }
    }); 
}

//删除元数据
function removes(id){
	var flag = confirm("确定删除元数据信息吗？");
	if (flag == true) {
		$.ajax({
			type : "GET",
			url : "http://" + window.location.host
					+ "/mateDataManager/removeRM?id=" + id,
			success : function(data) {
				load(1, 10);
				info(data);
			}
		});
	}
}

//修改状态
function changeState(id, state) {
	$.ajax({
		type : "GET",
		url : "http://" + window.location.host
				+ "/mateDataManager/changeStateRM?id=" + id + "&state=" + state,
		success : function(data) {
			load(1, 10);
			info(data);
		}
	});
}

//获取元数据
function getMetaData(id) {
	ininMetaDate();
	$.ajax({
		type : "GET",
		url : "http://" + window.location.host
				+ "/mateDataManager/getRMById?id=" + id,
		success : function(data) {
			var obj=eval("(" + data + ")");
			$("#metadataId").val(obj["id"]);
			$("#metadataId").attr("disabled",true);
            $("#metadataName").val(obj["name"]);
            $("#metadataType").val(obj["type"]);
            $("#metadataauthor").val(obj["author"]);
            $("#metadataState").val(obj["state"]);
            $("#metadataDesc").val(obj["content"]);
            $("#metadataRType").val(obj["resourceType"]);
            $("#metadataRPath").val(obj["path"]);
		}
	});
}