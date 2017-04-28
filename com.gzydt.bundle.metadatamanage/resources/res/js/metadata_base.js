//页面初始化方法
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

//添加字段方法
function addField(){
	var rows=$("#field_table").find("tr").length-1
	$("#base_field_list").append("<tr>" +
			"<td class='active'><input type='checkbox' class='form-control' name='fieldId'></td>"+
			"<td class='success'><input type='text'  class='form-control required' id='fid"+(rows)+"' name='fid"+(rows)+"' size='7' /></td>"+
			"<td class='warning'><input type='text' class='form-control required' id='fname"+(rows)+"' name='fname"+(rows)+"'  size='8'/></td>"+
			"<td class='danger'><input type='text' class=' form-control required' id='ftype"+(rows)+"' name='ftype"+(rows)+"' size='5'/></td>"+
			"<td class='info'><input type='text' class='form-control'  id='fdefault"+(rows)+"' size='5'/></td>"+
			"<td class='active'><select class='form-control'  id='fkey"+(rows)+"'><option value='1'>是</option><option value='0'>否</option></select></td>"+
			"<td class='success'><select class='form-control'  id='freq"+(rows)+"'><option value='1'>是</option><option value='0'>否</option></select></td>"+
			"<td class='warning'><input class='form-control' type='text'   id='findex"+(rows)+"' size='3'/></td>"+
			"<td class='danger'><input class='form-control' type='text'   id='frows"+(rows)+"' size='3'/></td>"+
			"<td class='info'><input class='form-control' type='text'  id='ftips"+(rows)+"' size='8'/></td>"+
			"<td class='active'><input class='form-control' type='text'  id='fdesc"+(rows)+"' size='10'/></td>"+
			"</tr>");
}
//全选
function selectAll() {
	var ckbs = $("input[name=allCheck]:checked");
	if (ckbs.size() > 0) {
		$("input[name='fieldId']").each(function() {
			this.checked = true;
		});
	} else {
		$("input[name='fieldId']").each(function() {
			this.checked = false;
		});
	} 
}
 
//删除字段
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

//提示信息
function info(message){
    var dialog=BootstrapDialog.show({
         message: message,
         title: "提示消息",
    });
    setTimeout(function(){dialog.close()},2000);
}

//加载基础元数据方法
function load(pageNum,pageSize){
	$("#base_list").empty();
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/mateDataManager/getAll?pageNum="
            +pageNum+"&pageSize="+pageSize+"&isStop="+-1+"&keyword=",
        success:function (data) {
        	var result=eval("(" + data + ")");
        	metadatas=result["data"];
        	for (var i = 0; i < metadatas.length; i++) {
        		var state=metadatas[i]["state"]=="1"?"启用":"停用";
        		var opState=metadatas[i]["state"]=="1"?"停用":"启用";
        		var opStateVal=metadatas[i]["state"]=="1"?"0":"1";
        		$("#base_list").append("<tr>" +
        				                     "<td class='active'>"+metadatas[i]["id"]+"</td>"+
        				                     "<td class='success'>"+metadatas[i]["name"]+"</td>"+
        				                     "<td class='warning'>"+metadatas[i]["type"]+"</td>"+
        				                     "<td class='danger'>"+metadatas[i]["author"]+"</td>"+
        				                     "<td class='info'>"+state+"</td>"+
        				                     "<td class='active'>"+metadatas[i]["content"]+"</td>"+
        				                     "<td class='success'><a href='#' onclick='getMetaData(\""+metadatas[i]["id"]+"\")' data-toggle='modal' data-target='#baseMeta_Info'>修改</a>" +
        				                     "|<a href='#' onclick='changeState(\""+metadatas[i]["id"]+"\", \""+opStateVal+"\")'>"+opState+"</a>" +
        				                     "|<a href='http://"+ window.location.host+ "/mateDataManager/output?id="+metadatas[i]["id"]+"'>导出</a>" +
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

//保存或者修改方法
function saveOrUpdate(){
	var validateResult = $('#baseMetaDataForm').validate({
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
				if(element[0].name == ("fid"+i)){
					$("#emsgf").show();
					$("#emsgf").html(error);
				}else if(element[0].name == ("fname"+i)){
					$("#emsgf").show();
					$("#emsgf").html(error);
				}else if(element[0].name == ("ftype"+i)){
					$("#emsgf").show();
					$("#emsgf").html(error);
				}else{
					$("#emsgf").hide();
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
		metaDataField.default=$("#fdefault"+i).val();
		metaDataField.key=$("#fkey"+i).val();
		metaDataField.req=$("#freq"+i).val();
		metaDataField.index=$("#findex"+i).val()==""?0:$("#findex"+i).val();
		metaDataField.rows=$("#frows"+i).val()==""?0:$("#frows"+i).val();
		metaDataField.tips=$("#ftips"+i).val();
		metaDataField.desc=$("#fdesc"+i).val();
		var json = JSON.stringify(metaDataField);
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
	    if (!re.test($("#frows"+i).val()))
	    {
	    	$("#emsg").show();
			$("#emsg").html("行数请输入正确的整数值");
			$("#findex"+i).val();
	        return;
	    }else{
	    	$("#emsg").hide();
	    }
		arrayObj.push(json);
	}
	$.ajax({
        type:"POST",
        url:"http://"+window.location.host+"/mateDataManager/saveOrUpdate",
        data:"id="+$("#metadataId").val()+"&name="+$("#metadataName").val()+"&type="+$("#metadataType").val()
             +"&author="+$("#metadataauthor").val()+"&state="+$("#metadataState").val()+"&desc="+$("#metadataDesc").val()+
             "&acdValue="+arrayObj,
        success:function (data) {
        	load(1,10);
        	$('#baseMeta_Info').modal('hide');
        	info(data);
        }
    }); 
}

//删除元数据方法
function remove(id){
	var flag = confirm("确定删除元数据信息吗？");
	if (flag == true) {
		$.ajax({
			type : "GET",
			url : "http://" + window.location.host
					+ "/mateDataManager/remove?id=" + id,
			success : function(data) {
				load(1, 10);
				info(data);
			}
		});
	}
}

//改变状态
function changeState(id, state) {
	$.ajax({
		type : "GET",
		url : "http://" + window.location.host
				+ "/mateDataManager/changeState?id=" + id + "&state=" + state,
		success : function(data) {
			load(1, 10);
			info(data);
		}
	});
}

//初始化元数据数据
function ininMetaDate(){
	changeFormInfo(1);
	$("#metadataId").val("");
	$("#metadataId").removeAttr("disabled");
    $("#metadataName").val("");
    $("#metadataType").val("");
    $("#metadataauthor").val("");
    $("#metadataState").val("1");
    $("#metadataDesc").val("");
    $("#base_field_list").html("");
    $("#gmetadataType").val("");
    $("#gmetadataauthor").val("");
    $("#gmetadataState").val("1");
}

//根据ID获取元数据
function getMetaData(id) {
	ininMetaDate();
	$.ajax({
		type : "GET",
		url : "http://" + window.location.host
				+ "/mateDataManager/getById?id=" + id,
		success : function(data) {
			var obj=eval("(" + data + ")");
			var ocd=obj["OCD"];
			$("#metadataId").val(ocd["id"]);
			$("#metadataId").attr("disabled",true);
            $("#metadataName").val(ocd["name"]);
            $("#metadataType").val(ocd["type"]);
            $("#metadataauthor").val(ocd["author"]);
            $("#metadataState").val(ocd["state"]);
            $("#metadataDesc").val(ocd["content"]);
            var ad=obj["AD"];
            var rows=$("#field_table").find("tr").length-1
            for (var i = 0; i < ad.length; i++) {
            	var isPrimary=ad[i]["isPrimary"];
            	var isNull=ad[i]["isNull"];
            	$("#base_field_list").append("<tr>" +
            			"<td class='active'><input type='checkbox' class='form-control' name='fieldId'></td>"+
            			"<td class='success'><input type='text' class='form-control' class='required' name='fid"+(rows)+"' id='fid"+(rows)+"' size='7' value='"+ad[i]["fieldId"]+"' /></td>"+
            			"<td class='warning'><input type='text' class='form-control required' name='fname"+(rows)+"' id='fname"+(rows)+"' value='"+ad[i]["name"]+"' size='8'/></td>"+
            			"<td class='danger'><input type='text' class='form-control required' name='ftype"+(rows)+"' id='ftype"+(rows)+"' value='"+ad[i]["type"]+"' size='5'/></td>"+
            			"<td class='info'><input type='text' class='form-control' id='fdefault"+(rows)+"' value='"+ad[i]["defaultValue"]+"' size='5'/></td>"+
            			"<td class='active'><select class='form-control' id='fkey"+(rows)+"'>" +
            					"<option value='1' "+(isPrimary==1?"selected='selected'":"")+">是</option>" +
            					"<option value='0' "+(isPrimary==0?"selected='selected'":"")+">否</option>" +
            				"</select></td>"+
            			"<td class='success'><select class='form-control' id='freq"+(rows)+"'>" +
            					"<option value='1' "+(isNull==1?"selected='selected'":"")+">是</option>" +
            					"<option value='0' "+(isNull==0?"selected='selected'":"")+">否</option>" +
            				"</select></td>"+
            			"<td class='warning'><input class='form-control' type='text' id='findex"+(rows)+"' value='"+ad[i]["indexs"]+"' size='3'/></td>"+
            			"<td class='danger'><input class='form-control' type='text' id='frows"+(rows)+"' value='"+ad[i]["rowNum"]+"' size='3'/></td>"+
            			"<td class='info'><input class='form-control' type='text' id='ftips"+(rows)+"' value='"+ad[i]["tip"]+"' size='8'/></td>"+
            			"<td class='active'><input class='form-control' type='text' id='fdesc"+(rows)+"' value='"+ad[i]["content"]+"' size='10'/></td>"+
            			"</tr>");
            	rows++;
			}
		}
	});
}

//上传元数据
function upload(){
     $.ajaxFileUpload
     (
         {
             url: '/mateDataManager/fileOperate', //用于文件上传的服务器端请求地址
             secureuri: false, //是否需要安全协议，一般设置为false
             fileElementId: 'file1', //文件上传域的ID
             dataType: 'json', //返回值类型 一般设置为json
             success: function (data, status)  //服务器成功响应处理函数
             {
            	 $("#fileName").val(data["fileName"]);
            	 info("上传成功");
             }
         }
     )
     return false;
}

//提取元数据
function pick(){
	var validateResult = $('#getBaseMetaForm').validate({
		errorPlacement:function(error,element) {
			//设置错误提示信息显示位置
			if(element[0].name == "gmetadataType"){
				$("#gemsg").show();
				$("#gemsg").html(error);
			}else{
				$("#gemsg").hide();
			}
	   }
	}).form();
	if(!validateResult){
		//校验不通过
		return false;
	}
	$.ajax({
		type : "POST",
		url : "http://" + window.location.host
				+ "/mateDataManager/pick",
		data:"fileName="+$("#fileName").val()+"&type="+$("#gmetadataType").val()+
		     "&author="+$("#gmetadataauthor").val()+"&state="+$("#gmetadataState").val(),
		success : function(data) {
			$('#getBaseMeta_Info').modal('hide');
			load(1,10);
			info(data);
		}
	});
}

//改变表单内容
function changeFormInfo(num){
	if(num==1){
		$("#basicFormInfoDiv").show();
		$("#fieldFormInfoDiv").hide();
		$("#basicFormInfoLi").attr("class","active");
		$("#fieldFormInfoLi").removeAttr("class");
	}else{
		$("#basicFormInfoDiv").hide();
		$("#fieldFormInfoDiv").show();
		$("#basicFormInfoLi").removeAttr("class");
		$("#fieldFormInfoLi").attr("class","active");
	}
}

