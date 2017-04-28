//隐藏字段数组
var hideFields;
//加载数据
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
//添加字段
function addField(){
	var rows=$("#field_table").find("tr").length-1
	$("#base_field_list").append("<tr>" +
			"<td class='active'><input type='checkbox' name='fieldId'></td>"+
			"<td class='success'><input type='text' id='fid"+(rows)+"' name='fid"+(rows)+"' class='form-control required' size='7' /></td>"+
			"<td class='warning'><input type='text' id='fname"+(rows)+"' name='fname"+(rows)+"' class=' form-control required' size='8'/></td>"+
			"<td class='danger'><select class=' form-control' id='ftype"+(rows)+"'>" +
					"<option value='int'>int</option>" +
					"<option value='double'>double</option>" +
					"<option value='String'>String</option>" +
					"<option value='date'>date</option>" +
					"</select></td>"+
			"<td class='info'><input type='text' id='flength"+(rows)+"' name='flength"+(rows)+"' class=' form-control' size='5'/></td>"+
			"<td class='active'><input type='text' class=' form-control' id='fdefault"+(rows)+"' size='5'/></td>"+
			"<td class='success'><select class=' form-control' id='fkey"+(rows)+"'><option value='1'>是</option><option value='0'>否</option></select></td>"+
			"<td class='warning'><select class=' form-control' id='freq"+(rows)+"'><option value='1'>是</option><option value='0'>否</option></select></td>"+
			"<td class='danger'><input class=' form-control' type='text' id='findex"+(rows)+"' size='3'/></td>"+
			"<td class='info'><input class=' form-control' type='text' id='fdesc"+(rows)+"' size='10'/></td>"+
			"</tr>");
}
//添加自定义键值
function addKey(){
	var rows=$("#customize_table").find("tr").length-1
	$("#customize_list").append("<tr>" +
			"<td class='active'><input type='checkbox' name='customizeId'></td>"+
			"<td class='success'><input type='text' id='fkeys"+(rows)+"' name='fkeys"+(rows)+"' class='form-control required' /></td>"+
			"<td class='warning'><input type='text' id='fvalue"+(rows)+"' name='fvalue"+(rows)+"' class=' form-control required'/></td>"+
			"</tr>");
}
//全选
function selectAll(name,fieldName) {
	var ckbs = $("input[name="+name+"]:checked");
	if (ckbs.size() > 0) {
		$("input[name="+fieldName+"]").each(function() {
			this.checked = true;
		});
	} else {
		$("input[name="+fieldName+"]").each(function() {
			this.checked = false;
		});
	} 
}
//移除字段
function removeField(){
	var ckbs = $("input[name=fieldId]:checked");
	if (ckbs.size() == 0) {
		info("未选中要删除的行！");
		return;
	}
	ckbs.each(function() {
		$(this).parent().parent().remove();
	});
}
//移除自定义键值对
function removeKey(){
	var ckbs = $("input[name=customizeId]:checked");
	if (ckbs.size() == 0) {
		info("未选中要删除的行！");
		return;
	}
	ckbs.each(function() {
		$(this).parent().parent().remove();
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
//初始化元数据
function ininMetaDate(){
	$("#goTable").click(function(){
		var goValue = $('#goValuTable').val();
		var reNum = /^\d*$/;
		//输入的不是数字或者为空
		if(!reNum.test(goValue) || goValue==""){
			goValue=1;
		}
		changeResource(goValue,10);
	});
	$("#goPre").click(function(){
		var goValue = $('#goValuPre').val();
		var reNum = /^\d*$/;
		//输入的不是数字或者为空
		if(!reNum.test(goValue) || goValue==""){
			goValue=1;
		}
		preview(goValue,10);
	});
	$("#goField").click(function(){
		var goValue = $('#goValuField').val();
		var reNum = /^\d*$/;
		//输入的不是数字或者为空
		if(!reNum.test(goValue) || goValue==""){
			goValue=1;
		}
		previewField(goValue,10);
	});
	changeFormInfo(1);
	hideFields="";
	$("#metadataId").val("");
	$("#metadataId").removeAttr("disabled");
	$("#tableName").val("");
    $("#metadataName").val("");
    $("#metadataType").val("");
    $("#metadataauthor").val("");
    $("#metadataState").val("1");
    $("#metadataDesc").val("");
    $("#metadataOrg").val("");
    $("#base_field_list").html("");
    $("#smetadataId").val("");
	$("#smetadataId").removeAttr("disabled");
    $("#metadataResource").empty();
    $("#emsg").html("");
    backPick();
    $("#customize_list").empty();
    $("#smetadataResource").empty();
    $("#preview_btn").hide();
    $.ajax({
		type : "GET",
		url : "http://" + window.location.host
				+ "/mateDataManager/getAllResource",
		async: false,
		success : function(data) {
			var metadatas=eval("(" + data + ")");
			for (var i = 0; i < metadatas.length; i++) {
				$("#metadataResource").append("<option value='"+metadatas[i]["id"]+"'>"+metadatas[i]["name"]+"</option>");
				$("#smetadataResource").append("<option value='"+metadatas[i]["id"]+"'>"+metadatas[i]["name"]+"</option>");
			}
		}
	});
    changeResource(1,10);
}
//修改资源
function changeResource(pageNum,pageSize){
	$("#tables_list").empty();
	$.ajax({
		type : "GET",
		url : "http://" + window.location.host
				+ "/mateDataManager/getTables?resource="+$("#smetadataResource").val()+"&pageNum="+pageNum+"&pageSize="+pageSize,
		success : function(data) {
			var result=eval("(" + data + ")");
			metadatas=result["tables"];
			for (var i = 0; i < metadatas.length; i++) {
						$("#tables_list").append("<tr>" +
								"<td class='acative'><input type='checkbox' name='tableName' value='"+metadatas[i]+"'></td>" +
								"<td class='success'>"+(i+1)+"</td>"+
								"<td class='warning'>"+metadatas[i]+"</td>"+
						"</tr>");
			}
			pageNum=parseInt(pageNum); 
        	var pageCount=result["pageCount"];
        	if(pageNum>=pageCount){
        		pageNum=pageCount;
        	}
        	$("#pageULTable").empty();
        	var pagehtml = '<li style="float:left;font-size: 12px;height:35px; line-height:35px;margin-right:20px">'+
			   '<div style="font-size:14px">共：'+pageCount+'&nbsp;页</div>'+
			   '</li>';
        	if(pageNum>1){
        		pagehtml+= '<li><a href="#" onclick="changeResource(\''+(pageNum-1)+'\',\''+10+'\')">上一页</a></li>';
        	}
        	for(var i=0;i<pageCount;i++){
        		if(i>=(pageNum-3) && i<(pageNum+3)){
        			if(i==pageNum-1){
        				pagehtml+= '<li class="active"><a href="#" onclick="changeResource(\''+(i+1)+'\',\''+10+'\')">'+(i+1)+'</a></li>';
        			}else{
        				pagehtml+= '<li><a href="#" onclick="changeResource(\''+(i+1)+'\',\''+10+'\')">'+(i+1)+'</a></li>';
        			}
        		}
        	}
        	if(pageNum<pageCount){
        		pagehtml+= '<li><a href="#" onclick="changeResource(\''+(pageNum+1)+'\',\''+10+'\')">下一页</a></li>';
        	}
        	$("#pageULTable").append(pagehtml);
		}
	});
}

//预览字段
function previewField(pageNum,pageSize){
	$("#fields_list").empty();
	var table="";
	var ckbs = $("input[name=tableName]:checked");
	if (ckbs.size() == 0) {
		info("未选中要预览的表格！");
		return;
	}
	if (ckbs.size() > 1) {
		info("一次只能选择一个表格进行预览！");
		return;
	}
	ckbs.each(function() {
		table+=$(this).val();
	});
	$.ajax({
		type : "GET",
		url : "http://" + window.location.host
				+ "/mateDataManager/previewField?resource="+$("#smetadataResource").val()+"&tableName="+table+"&pageNum="+pageNum+"&pageSize="+pageSize,
		success : function(data) {
			var result=eval("(" + data + ")");
			metadatas=result["lastfields"];
			for (var i = 0; i < metadatas.length; i++) {
						$("#fields_list").append("<tr>" +
								"<td class='acative'><input type='checkbox' name='fieldName' value='"+metadatas[i]+"'></td>" +
								"<td class='success'>"+(i+1)+"</td>"+
								"<td class='warning'>"+metadatas[i]+"</td>"+
						"</tr>");
			}
			pageNum=parseInt(pageNum); 
        	var pageCount=result["pageCount"];
        	if(pageNum>=pageCount){
        		pageNum=pageCount;
        	}
        	$("#pageULField").empty();
        	var pagehtml = '<li style="float:left;font-size: 12px;height:35px; line-height:35px;margin-right:20px">'+
			   '<div style="font-size:14px">共：'+pageCount+'&nbsp;页</div>'+
			   '</li>';
        	if(pageNum>1){
        		pagehtml+= '<li><a href="#" onclick="previewField(\''+(pageNum-1)+'\',\''+10+'\')">上一页</a></li>';
        	}
        	for(var i=0;i<pageCount;i++){
        		if(i>=(pageNum-3) && i<(pageNum+3)){
        			if(i==pageNum-1){
        				pagehtml+= '<li class="active"><a href="#" onclick="previewField(\''+(i+1)+'\',\''+10+'\')">'+(i+1)+'</a></li>';
        			}else{
        				pagehtml+= '<li><a href="#" onclick="previewField(\''+(i+1)+'\',\''+10+'\')">'+(i+1)+'</a></li>';
        			}
        		}
        	}
        	if(pageNum<pageCount){
        		pagehtml+= '<li><a href="#" onclick="previewField(\''+(pageNum+1)+'\',\''+10+'\')">下一页</a></li>';
        	}
        	$("#pageULField").append(pagehtml);
			
		}
	});
	$("#save_btn").hide();
	$("#previewField_btn").hide();
	$("#preview_btn").show();
	$("#pickInfo").hide();
	$("#previewFieldInfo").show();
	$("#previewInfo").hide();
}

//保存和修改
function saveOrUpdate(){
	var validateResult = $('#typeMetaDataForm').validate({
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
			}else{
				$("#emsg").hide();
			}
	   }
	}).form();
	if(!validateResult){
		//校验不通过
		return false;
	}
	var rows=$("#field_table").find("tr").length-1;
	var arrayObj = new Array();
	for (var i = 0; i < rows; i++) {
		var validateResult = $('#baseMetaDataFieldForm').validate({
			errorPlacement:function(error,element) {
				//设置错误提示信息显示位置
				if(element[0].name == "fid"+i){
					$("#emsgf").show();
					$("#emsgf").html(error);
				}else if(element[0].name == "fname"+i){
					$("#emsgf").show();
					$("#emsgf").html(error);
				}else{
					$("#emsg").hide();
				}
		   }
		}).form();
		if(!validateResult){
			//校验不通过
			return false;
		}
		var metaDataField=new Object();
		metaDataField.id=$("#fid"+i).val();
		metaDataField.name=$("#fname"+i).val();
		metaDataField.type=$("#ftype"+i).val();
		metaDataField.length=$("#flength"+i).val()==""?10:$("#flength"+i).val();
		metaDataField.default=$("#fdefault"+i).val();
		metaDataField.key=$("#fkey"+i).val();
		metaDataField.req=$("#freq"+i).val();
		metaDataField.index=$("#findex"+i).val()==""?0:1;
		var re = /^[0-9]*$/;
	    if (!re.test($("#findex"+i).val()))
	    {
	    	$("#emsg").show();
			$("#emsg").html("索引值请输入正确的整数值");
			$("#findex"+i).val();
	        return;
	    }else{
	    	$("#emsg").hide();
	    }
	    if (!re.test($("#flength"+i).val()))
	    {
	    	$("#emsg").show();
			$("#emsg").html("长度请输入正确的整数值");
			$("#findex"+i).val();
	        return;
	    }else{
	    	$("#emsg").hide();
	    }
		metaDataField.desc=$("#fdesc"+i).val();
		var json = JSON.stringify(metaDataField);
		arrayObj.push(json);
	}
	var crows=$("#customize_table").find("tr").length-1;
	var carrayObj = new Array();
	for (var i = 0; i < crows; i++) {
		var validateResult = $('#customizeInfoForm').validate({
			errorPlacement:function(error,element) {
				//设置错误提示信息显示位置
				if(element[0].name == "fkeys"+i){
					$("#cmsgf").show();
					$("#cmsgf").html(error);
				}else if(element[0].name == "fvalue"+i){
					$("#cmsgf").show();
					$("#cmsgf").html(error);
				}else{
					$("#cmsg").hide();
				}
		   }
		}).form();
		if(!validateResult){
			//校验不通过
			return false;
		}
		var customize=new Object();
		customize.key=$("#fkeys"+i).val();
		customize.value=$("#fvalue"+i).val();
		var json = JSON.stringify(customize);
		carrayObj.push(json);
	}
	var re1=/^[A-Za-z]{1}[\w\d]*$/;
    if (!re1.test($("#tableName").val()))
    {
    	$("#emsg").show();
		$("#emsg").html("表名必须是英文开头，只能是数字和字母");
        return;
    }
	$.ajax({
        type:"POST",
        url:"http://"+window.location.host+"/mateDataManager/saveOrUpdateTM",
        data:"id="+$("#metadataId").val()+"&name="+$("#metadataName").val()+"&type="+$("#metadataType").val()
             +"&author="+$("#metadataauthor").val()+"&state="+$("#metadataState").val()+"&desc="+$("#metadataDesc").val()+
             "&org="+$("#metadataOrg").val()+"&resource="+$("#metadataResource").val()+"&tableName="+$("#tableName").val()+"&fields="+arrayObj+"&customize="+carrayObj,
        success:function (data) {
        	load(1,10);
        	$('#baseMeta_Info').modal('hide');
        	info(data);
        }
    }); 
}
//提取
function save(){
	var tables="";
	var ckbs = $("input[name=tableName]:checked");
	if (ckbs.size() == 0) {
		info("未选中要提取的表格！");
		return;
	}
	ckbs.each(function() {
		tables+=$(this).val()+",";
	});
	tables=tables.substring(0,tables.length-1);
	$.ajax({
        type:"POST",
        url:"http://"+window.location.host+"/mateDataManager/save",
        data:"resource="+$("#smetadataResource").val()+"&tables="+tables,
        success:function (data) {
        	load(1,10);
        	$('#getBaseMeta_Info').modal('hide');
        	info(data);
        }
    }); 
}
//加载信息
function load(pageNum,pageSize){
	$("#base_list").empty();
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/mateDataManager/getAllTM?pageNum="
            +pageNum+"&pageSize="+pageSize+"&isStop="+-1+"&keyword=",
        success:function (data) {
        	var result=eval("(" + data + ")");
        	metadatas=result["data"];
        	for (var i = 0; i < metadatas.length; i++) {
        		var state=metadatas[i]["state"]=="1"?"启用":"停用";
        		var opState=metadatas[i]["state"]=="1"?"停用":"启用";
        		var opStateVal=metadatas[i]["state"]=="1"?"0":"1";
        		$("#base_list").append("<tr>" +
        				                     "<td class='active'>"+metadatas[i]["tableName"]+"</td>"+
        				                     "<td class='success'>"+metadatas[i]["name"]+"</td>"+
        				                     "<td class='warning'>"+metadatas[i]["org"]+"</td>"+
        				                     "<td class='danger'>"+metadatas[i]["type"]+"</td>"+
        				                     "<td class='info'>"+metadatas[i]["author"]+"</td>"+
        				                     "<td class='active'>"+state+"</td>"+
        				                     "<td class='success'>"+metadatas[i]["content"]+"</td>"+
        				                     "<td class='warning'><a href='#' onclick='getMetaData(\""+metadatas[i]["id"]+"\")' data-toggle='modal' data-target='#baseMeta_Info'>修改</a>" +
        				                     "|<a href='#' onclick='changeState(\""+metadatas[i]["id"]+"\", \""+opStateVal+"\")'>"+opState+"</a>" +
        				                     "|<a href='#' onclick='synchronous(\""+metadatas[i]["id"]+"\")'>同步</a>" +
        				                     "|<a href='#' onclick='association(\""+metadatas[i]["id"]+"\")' data-toggle='modal' data-target='#relation_info'>关联</a>" +
        				                     "|<a href='#' onclick='remove(\""+metadatas[i]["id"]+"\")'>删除</a></td>"+
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
//移除元数据
function remove(id){
	var flag = confirm("确定删除元数据信息吗？");
	if (flag == true) {
		$.ajax({
			type : "GET",
			url : "http://" + window.location.host
					+ "/mateDataManager/removeTM?id=" + id,
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
				+ "/mateDataManager/changeStateTM?id=" + id + "&state=" + state,
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
				+ "/mateDataManager/getTMById?id=" + id,
		success : function(data) {
			var obj=eval("(" + data + ")");
			var table=obj["TABLE"];
			$("#metadataId").val(table["id"]);
			$("#metadataId").attr("disabled",true);
			$("#tableName").val(table["tableName"]);
            $("#metadataName").val(table["name"]);
            $("#metadataOrg").val(table["org"]);
            $("#metadataType").val(table["type"]);
            $("#metadataauthor").val(table["author"]);
            $("#metadataState").val(table["state"]);
            $("#metadataDesc").val(table["content"]);
            $("#metadataResource").val(table["resourceMetaDataId"]);
            var fields=obj["FIELDS"];
            var rows=$("#field_table").find("tr").length-1
            for (var i = 0; i < fields.length; i++) {
            	var isPrimary=fields[i]["isPrimary"];
            	var isNull=fields[i]["isNull"];
            	var isType=fields[i]["type"];
            	$("#base_field_list").append("<tr>" +
            			"<td class='active'><input type='checkbox' name='fieldId'></td>"+
            			"<td class='success'><input type='text' id='fid"+(rows)+"' name='fid"+(rows)+"' class='form-control required' size='7' value='"+fields[i]["fieldId"]+"' /></td>"+
            			"<td class='warning'><input type='text' id='fname"+(rows)+"' name='fname"+(rows)+"' class='form-control required' value='"+fields[i]["name"]+"' size='8'/></td>"+
            			"<td class='danger'><select class=' form-control' id='ftype"+(rows)+"'>" +
		    					"<option value='int' "+(isType=='int'?"selected='selected'":"")+">int</option>" +
		    					"<option value='double' "+(isType=='double'?"selected='selected'":"")+">double</option>" +
		    					"<option value='String' "+(isType=='String'?"selected='selected'":"")+">String</option>" +
		    					"<option value='date' "+(isType=='date'?"selected='selected'":"")+">date</option>" +
    					"</select></td>"+
    					"<td class='info'><input class=' form-control' type='text' id='flength"+(rows)+"' name='flength"+(rows)+"'  value='"+fields[i]["length"]+"' size='5'/></td>"+
            			"<td class='active'><input type='text' class=' form-control' id='fdefault"+(rows)+"' value='"+fields[i]["defaultValue"]+"' size='5'/></td>"+
            			"<td class='success'><select class=' form-control' id='fkey"+(rows)+"'>" +
            					"<option value='1' "+(isPrimary==1?"selected='selected'":"")+">是</option>" +
            					"<option value='0' "+(isPrimary==0?"selected='selected'":"")+">否</option>" +
            				"</select></td>"+
            			"<td class='warning'><select class=' form-control' id='freq"+(rows)+"'>" +
            					"<option value='1' "+(isNull==1?"selected='selected'":"")+">是</option>" +
            					"<option value='0' "+(isNull==0?"selected='selected'":"")+">否</option>" +
            				"</select></td>"+
            			"<td class='danger'><input class=' form-control' type='text' id='findex"+(rows)+"' value='"+fields[i]["indexs"]+"' size='3'/></td>"+
            			"<td class='info'><input class=' form-control' type='text' id='fdesc"+(rows)+"' value='"+fields[i]["content"]+"' size='10'/></td>"+
            			"</tr>");
            	rows++;
			}
            var customize_table=obj=eval("(" +table["customize"]+ ")");
            var crows=$("#customize_table").find("tr").length-1;
            for (var i = 0; i < customize_table.length; i++) {
            	$("#customize_list").append("<tr>" +
            			"<td class='active'><input type='checkbox' name='customizeId'></td>"+
            			"<td class='success'><input type='text' value='"+customize_table[i]["key"]+"' id='fkeys"+(crows)+"' name='fkeys"+(crows)+"' class='form-control required' /></td>"+
            			"<td class='warning'><input type='text' value='"+customize_table[i]["value"]+"' id='fvalue"+(crows)+"' name='fvalue"+(crows)+"' class=' form-control required'/></td>"+
            			"</tr>");
            	crows++;
			}
		}
	});
}
//同步元数据
function synchronous(id) {
	$.ajax({
		type : "GET",
		url : "http://" + window.location.host
				+ "/mateDataManager/synchronous?id=" + id ,
		success : function(data) {
			load(1, 10);
			info(data);
		}
	});
}
//更改表单信息
function changeFormInfo(num){
	if(num==1){
		$("#basicFormInfoDiv").show();
		$("#basicFormInfoLi").attr("class","active");
		$("#fieldFormInfoDiv").hide();
		$("#fieldFormInfoLi").removeAttr("class");
		$("#customizeInfoDiv").hide();
		$("#customizeInfoLi").removeAttr("class");
	}if(num==2){
		$("#customizeInfoDiv").show();
		$("#customizeInfoLi").attr("class","active");
		$("#basicFormInfoDiv").hide();
		$("#basicFormInfoLi").removeAttr("class");
		$("#fieldFormInfoDiv").hide();
		$("#fieldFormInfoLi").removeAttr("class");
	}else if(num==0){
		$("#fieldFormInfoDiv").show();
		$("#fieldFormInfoLi").attr("class","active");
		$("#basicFormInfoDiv").hide();
		$("#basicFormInfoLi").removeAttr("class");
		$("#customizeInfoDiv").hide();
		$("#customizeInfoLi").removeAttr("class");
	}
}
//预览
function preview(pageNum,pageSize){
	var table="";
	var ckbs = $("input[name=tableName]:checked");
	if (ckbs.size() == 0) {
		info("未选中要预览的表格！");
		return;
	}
	if (ckbs.size() > 1) {
		info("一次只能选择一个表格进行预览！");
		return;
	}
	ckbs.each(function() {
		table+=$(this).val();
	});
	var ckbs1 = $("input[name=fieldName]:checked");
	ckbs1.each(function() {
		hideFields+=$(this).val()+",";
	});
	hideFields=hideFields.substring(0,hideFields.length-1);
	$.ajax({
		type : "GET",
		url : "http://" + window.location.host
				+ "/mateDataManager/preview?resource="+$("#smetadataResource").val()+"&tableName="+table+"&pageNum="+pageNum+"&pageSize="+pageSize+"&hideFields="+hideFields,
		success : function(data) {
			var obj=eval("(" + data + ")");
			var fields=obj["lastfields"];
			$("#cb_field").empty();
			$("#preview_head").empty();
			$("#preview_body").empty();
			var tableHeadHtml = "<tr>";
			var cb_field="";
			for (var i = 0; i < fields.length; i++) {
				tableHeadHtml += "<td>"+fields[i]+"</td>";
			}
			$("#cb_field").html(cb_field);
			$("#preview_head").append(tableHeadHtml + "</tr>");
			var tableValues=obj["tableValue"];
			for (var i = 0; i < tableValues.length; i++) {
				var tableHtml = "<tr>";
				for (var j = 0; j < tableValues[i].length; j++) {
					tableHtml += "<td>"+tableValues[i][j]+"</td>";
				}
				$("#preview_body").append(tableHtml + "</tr>");
			}
			$("#footPage").attr("colspan",fields.length);
			pageNum=parseInt(pageNum); 
        	var pageCount=obj["pageCount"];
        	if(pageNum>=pageCount){
        		pageNum=pageCount;
        	}
        	$("#pageULPre").empty();
        	var pagehtml = '<li style="float:left;font-size: 12px;height:35px; line-height:35px;margin-right:20px">'+
			   '<div style="font-size:14px">共：'+pageCount+'&nbsp;页</div>'+
			   '</li>';
        	if(pageNum>1){
        		pagehtml+= '<li><a href="#" onclick="preview(\''+(pageNum-1)+'\',\''+10+'\')">上一页</a></li>';
        	}
        	for(var i=0;i<pageCount;i++){
        		if(i>=(pageNum-3) && i<(pageNum+3)){
        			if(i==pageNum-1){
        				pagehtml+= '<li class="active"><a href="#" onclick="preview(\''+(i+1)+'\',\''+10+'\')">'+(i+1)+'</a></li>';
        			}else{
        				pagehtml+= '<li><a href="#" onclick="preview(\''+(i+1)+'\',\''+10+'\')">'+(i+1)+'</a></li>';
        			}
        		}
        	}
        	if(pageNum<pageCount){
        		pagehtml+= '<li><a href="#" onclick="preview(\''+(pageNum+1)+'\',\''+10+'\')">下一页</a></li>';
        	}
        	$("#pageULPre").append(pagehtml);
		}
	});
	$("#save_btn").hide();
	$("#preview_btn").hide();
	$("#previewFiled_btn").hide();
	$("#pickInfo").hide();
	$("#previewFieldInfo").hide();
	$("#previewInfo").show();
}
//返回提取
function backPick(){
	hideFields="";
	$("#save_btn").show();
	$("#pickInfo").show();
	$("#previewFieldInfo").hide();
	$("#previewField_btn").show();
	$("#preview_btn").hide();
	$("#previewInfo").hide();
}
//隐藏字段
function hideField(){
	hideFields="";
	var ckbs = $("input[name=fieldNames]:checked");
	ckbs.each(function() {
		hideFields+=$(this).val()+",";
	});
	hideFields=hideFields.substring(0,hideFields.length-1);
	preview(1,10);
}
//关联关系
function association(id){
	var currendMetadata=id;
	$("#relation").val(1);
	$("#relationDesc").val("");
	$("#fieldB").empty();
	var fieldAHtml="<option>--请选择--</option>";
	$("#fieldB").append(fieldAHtml);
	$.ajax({
		type : "GET",
		async: false,
		url : "http://" + window.location.host
				+ "/mateDataManager/getTMById?id="+id,
		success : function(data) {
			var obj=eval("(" + data + ")");
			$("#metadataA").val(obj["TABLE"].id);
			$("#fieldA").empty();
			var fieldAHtml;
			for (var i = 0; i < obj["FIELDS"].length; i++) {
				fieldAHtml+="<option value='"+obj["FIELDS"][i].id+"'>"+obj["FIELDS"][i].fieldId+"</option>";
			}
			$("#fieldA").append(fieldAHtml);
		}
	});
	$.ajax({
		type : "GET",
		async: false,
		url : "http://" + window.location.host
				+ "/mateDataManager/getMetaDataB",
		success : function(data) {
			var obj=eval("(" + data + ")");
			$("#metadataB").empty();
			var metadataBHtml="<option>--请选择--</option>";
			for (var i = 0; i < obj.length; i++) {
				if(obj[i].id!=currendMetadata){
					metadataBHtml+="<option value='"+obj[i].id+"'>"+obj[i].id+"</option>";
				}
			}
			$("#metadataB").append(metadataBHtml);
		}
	});
	$.ajax({
		type : "GET",
		async: false,
		url : "http://" + window.location.host
				+ "/mateDataManager/getRelationById?metaDataid="+currendMetadata,
		success : function(data) {
			if(data!=null){
				var obj=eval("(" + data + ")");
				$("#metadataB").val(obj.metadataB);
				changeFieldB();
				$("#fieldA").val(obj.fieldA);
				$("#fieldB").val(obj.fieldB);
				$("#relation").val(obj.type);
				$("#relationDesc").val(obj.content);
			}
		}
	});
}
//修改字段B
function changeFieldB(){
	var metadataBId=$("#metadataB").val();
	$.ajax({
		type : "GET",
		async: false,
		url : "http://" + window.location.host
				+ "/mateDataManager/getTMById?id="+metadataBId,
		success : function(data) {
			var obj=eval("(" + data + ")");
			$("#fieldB").empty();
			var fieldAHtml="<option>--请选择--</option>";
			for (var i = 0; i < obj["FIELDS"].length; i++) {
				fieldAHtml+="<option value='"+obj["FIELDS"][i].id+"'>"+obj["FIELDS"][i].fieldId+"</option>";
			}
			$("#fieldB").append(fieldAHtml);
		}
	});
}
//保存关联关系
function saveRelation(){
	var metaDataA=$("#metadataA").val();
	var metaDataB=$("#metadataB").val();
	var fieldA=$("#fieldA").val();
	var fieldB=$("#fieldB").val();
	var type=$("#relation").val();
	var content=$("#relationDesc").val();
	$.ajax({
		type : "POST",
		async: false,
		url : "http://" + window.location.host
				+ "/mateDataManager/saveRelation",
		data: "metadataA="+metaDataA+"&metadataB="+metaDataB+"&fieldA="+fieldA+"&fieldB="+fieldB
		      +"&type="+type+"&content="+content,
		success : function(data) {
			$('#relation_info').modal('hide');
			info(data);
		}
	});
}
