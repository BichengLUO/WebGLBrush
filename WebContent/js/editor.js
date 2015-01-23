(function($) {
	$.getQuery = function(query) {
		query = query.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
		var expr = "[\\?&]" + query + "=([^&#]*)";
		var regex = new RegExp(expr);
		var results = regex.exec(window.location.href);
		if (results !== null) {
			return results[1];
			return decodeURIComponent(results[1].replace(/\+/g, " "));
		} else {
			return false;
		}
	};
})(jQuery);

$(document).ready(function() {
	uid = $.getQuery('uid');
	mid = $.getQuery('mid');
	var alert_modal = function(title, content) {
		$('.modal-title').html(title);
		$('.modal-body').children('p').html(content);
		$('#alertModal').modal('show');
	};
	$('#import').click(function() {
		$('#fileopen').trigger('click');
	});
	$('#exit').click(function() {
		location.href = 'home.html?uid=' + uid;
	});
	$('#save_model').click(function() {
		if (!sculptgl.mesh_)
			return;
		var obj_data = Export.exportOBJ(sculptgl.mesh_);
		url = "save";
		$.post(url, {
			mid : mid,
			data : obj_data
		}, function(data) {
			if (data.result == 0) {
				alert_modal('好像出现问题', data.message);
			} else if (data.result == 1) {
				alert_modal('保存成功', data.message);
			}
		}, "json");
	});
	$('#export_obj').click(function() {
		if (!sculptgl.mesh_)
			return;
		var data = [ Export.exportOBJ(sculptgl.mesh_) ];
		var blob = new Blob(data, {
			type : 'text/plain;charset=utf-8'
		});
		saveAs(blob, 'yourMesh.obj');
	});
	$('#export_stl').click(function() {
		if (!sculptgl.mesh_)
			return;
		var data = [ Export.exportSTL(sculptgl.mesh_) ];
		var blob = new Blob(data, {
			type : 'text/plain;charset=utf-8'
		});
		saveAs(blob, 'yourMesh.stl');
	});
	$('#export_ply').click(function() {
		if (!sculptgl.mesh_)
			return;
		var data = [ Export.exportPLY(sculptgl.mesh_) ];
		var blob = new Blob(data, {
			type : 'text/plain;charset=utf-8'
		});
		saveAs(blob, 'yourMesh.ply');
	});

	url = 'filename';
	$.post(url, {
		mid : mid
	}, function(data) {
		if (data.result == 0) {
			alert_modal("好像出现问题", data.message);
		} else if (data.result == 1) {
			modelFileName = data.filename;
			murl = '/models/m' + mid + '/' + modelFileName + "?t=" + Math.random();
			sculptgl.loadModelFile(murl);
		}
	}, "json");
});