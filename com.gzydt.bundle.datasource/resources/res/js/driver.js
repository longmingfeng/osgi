window.onload=function(){
	loadDriverInfo();
}

function loadDriverInfo(){
	$("#driver_list").empty();
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/getDriverInfo/getInfo",
        success:function (data) {
        	var obj = eval("(" + data + ")");
        	for (var i = 1; i <= obj.length; i++) {
				$("#driver_list").append('<tr>'+
        				'<td>'+i+'</td>'+
        				'<td>'+obj[i-1]["driverName"]+'</td>'+
        				'<td>'+obj[i-1]["version"]+'</td>'+
        				'<td>'+obj[i-1]["bundleName"]+'</td>'+
        				'<td>'+obj[i-1]["provide"]+'</td>'+
        				'<td>'+obj[i-1]["context"]+'</td>'+
        			'</tr>');
			}
        }
    }); 	
}