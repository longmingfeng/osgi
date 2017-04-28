window.onload=function(){
	loadBundleInfo();
}

function loadBundleInfo(){
	$.ajax({
        type:"GET",
        global:false, 
        url:"http://"+window.location.host+"/getDriverInfo/getBundleInfo",
        success:function (data) {
           var obj = eval("(" + data + ")");
           for (var i = 0; i < obj.length; i++) {
        	   $("#bundle_list").append(' <div class="panel panel-default">'+
			     		  '<div onclick="hiddenBundle(\''+i+'\')" class="panel-heading" style="height:40px;" id="'+i+'">'+
				     		  '<span style="float: left;margin-right:100px">组件名称：'+obj[i]["name"]+'</span>'+
				     		  /*'<span style="float: left;margin-right:300px">组件描述：'+obj[i]["description"]+'</span>'+
				     		  '<span style="float: left;margin-right:100px">组件提供者：'+obj[i]["provide"]+'</span>'+*/
				     		  '<span style="float: right;margin-right:10px">版本号：'+obj[i]["version"]+'</span>'+
				     		  '<span id="h'+i+'" class="glyphicon glyphicon-plus" style="float: right"></span>'+
			     		  '</div>'+
			     		  '<div style="display:none" class="panel-body" id="bunleCont'+i+'">'+
			     		       '<table class="table tablesorter nicetable noauto">'+
			     				 '<thead>'+
			     				    '<tr>'+
			     				      '<td>序号</td>'+
			     				      '<td>实体名称</td>'+
			     				      '<td>持久化表名称</td>'+
			     				      //'<td>组件提供人</td>'+
			     				      '<td>状态</td>'+
			     				      //'<td>原因</td>'+
			     				      //'<td>组件描述</td>'+
			     				      '<td>查看持久化列信息</td>'+
			     				    '</tr>'+
			     				 '</thead>'+
			     				 '<tbody id="jpa_list'+i+'">'+
			     			     '</tbody>'+
			     			  '</table>'+
			     		  '</div>'+
			     		'</div>');
        	   selectJpa(1,obj[i]["name"],i);
		   }
        }
	});
	
}

function selectJpa(page,name,id){
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
	$("#jpa_list").empty();
	$(".list-group-item").css("background-color","#ffffff");
	$("#"+id).css("background-color","#D9EDF7");
	$.ajax({
        type:"GET",
        global:false, 
        url:"http://"+window.location.host+"/getDriverInfo/findJpa?name="+name,
        global:false, 
        success:function (data) {
        	var obj = eval("(" + data + ")");
        	for (var i = 1; i <= obj.length; i++) {
				$("#jpa_list"+id).append('<tr>'+
        				'<td>'+i+'</td>'+
        				'<td>'+obj[i-1]["entityName"]+'</td>'+
        				'<td>'+obj[i-1]["tableName"]+'</td>'+
        				//'<td>'+obj[i-1]["provide"]+'</td>'+
        				'<td>'+obj[i-1]["state"]+'</td>'+
        				//'<td>'+obj[i-1]["message"]+'</td>'+
        				//'<td>'+obj[i-1]["description"]+'</td>'+
        				'<td><button class="btn btn-primary btn-xs" data-toggle="modal" data-target="#columnModal" onclick="findColumn(\''+obj[i-1]["entityName"]+'\')">查看</button></td>'+
        			'</tr>');
			}
        }
    });
}


function findColumn(name){
	$("#column_list").empty();
	$.ajax({
        type:"GET",
        global:false, 
        url:"http://"+window.location.host+"/getDriverInfo/findColumn?name="+name,
        global:false, 
        success:function (data) {
        	var obj = eval("(" + data + ")");
        	for (var i = 1; i <= obj.length; i++) {
				$("#column_list").append('<tr>'+
        				'<td>'+i+'</td>'+
        				'<td>'+obj[i-1]["name"]+'</td>'+
        				'<td>'+obj[i-1]["type"]+'</td>'+
        				'<td>'+obj[i-1]["provide"]+'</td>'+
        				'<td>'+obj[i-1]["description"]+'</td>'+
        			'</tr>');
			}
        }
    });
}

function hiddenBundle(id){
	if($("#h"+id).attr("class")=="glyphicon glyphicon-minus"){
		$("#h"+id).attr("class", "glyphicon glyphicon-plus");
		$("#bunleCont"+id).hide();
	}else{
		$("#h"+id).attr("class", "glyphicon glyphicon-minus");
		$("#bunleCont"+id).show();
	}
}
