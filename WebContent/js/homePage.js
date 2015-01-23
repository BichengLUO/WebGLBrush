(function($){
    $.getQuery = function( query ) {
        query = query.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
        var expr = "[\\?&]"+query+"=([^&#]*)";
        var regex = new RegExp( expr );
        var results = regex.exec( window.location.href );
        if( results !== null ) {
            return results[1];
            return decodeURIComponent(results[1].replace(/\+/g, " "));
        } else {
            return false;
        }
    };
})(jQuery);

/** Load file */
var loadFile = function (event)
{
	$('#importModal').modal('show');
};

$(document).ready(function() {
	url = "login";
	uid = $.getQuery('uid');
	deleting_wrapper = null;
	var alert_modal = function(title, content) {
		$('.alertTitle').html(title);
		$('.alertBody').children('p').html(content);
		$('#alertModal').modal('show');
	};
	var refreshView = function(userData) {
		$('#user-names').children('br').before(userData.nickname);
		$('#user-names').children('small').children('span').after("  " + userData.username);
		$('#img-avatar').attr('src', userData.avatar_url);
		$('#storage').attr('aria-valuenow', userData.used_storage);
		$('#storage').css('width', userData.used_storage + '%');
		$('#storage_text').html('已使用' + userData.used_storage + 'MB空间');
		length = userData.models.length;
		for (var i = 0; i < length; i++) {
			var obj = $('#wrapper').clone();
			$(obj).css('display', 'float');
			$(obj).children('p').html(userData.models[i].model_name);
			$(obj).children('img').attr('src', userData.models[i].thumbnail_url);
			$(obj).children('div').children('button').attr('id', userData.models[i].model_id);
			$(obj).children('div').children('a.export_model').attr('href', userData.models[i].model_url);
			open_url = 'editor.html?uid=' + uid + '&mid=' + userData.models[i].model_id;
			$(obj).children('div').children('a.open_model').attr('href', open_url);
			$('#model-middle').append(obj);
		}
	};
	
	var del_action = function() {
		var mid = $(this).attr('id');
		deleting_wrapper = $(this).parent('div.btn-group').parent('div#wrapper');
		url = 'deletemodel';
		$.post(url, {
			mid : mid
		}, function(data) {
			if (data.result == 0) {
				alert_modal("好像出现问题", data.message);
			} else if (data.result == 1) {
				$(deleting_wrapper).fadeOut(500, function() {
					$(this).remove();
					deleting_wrapper = null;
					url = 'login';
					$.post(url, {
						uid : uid
					}, function(data) {
						if (data.result == 0) {
							alert_modal("好像出现问题", data.message);
						} else if (data.result == 1) {
							var userData = data;
							$('#storage').attr('aria-valuenow', userData.used_storage);
	            			$('#storage').css('width', userData.used_storage + '%');
	            			$('#storage_text').html('已使用' + userData.used_storage + 'MB空间');
						}
					}, "json");
				});
			}
		}, "json");
	};
	
	$('#btn-new').click(function() {
		$('#importModal').modal('show');
	});
	$('#btn-import').click(function() {
		$('#fileopen').trigger('click');
	});
	$('#fileopen').change(function(event) {
		loadFile(event);
	});
	
	$('#import').click(function() {
		var form_data = new FormData();
		if ($("#fileopen").prop("files").length > 0) {
			var file_data = $("#fileopen").prop("files")[0];
			form_data.append("file", file_data);
		}
		var model_name = $('#import-filename').val();
		form_data.append("model_name", model_name);
		form_data.append("uid", uid);
		$.ajax({
            url: "import",
            dataType: 'json',
            cache: false,
            contentType: false,
            processData: false,
            data: form_data,
            type: 'post',
            success: function(data) {
            	$('#fileopen').replaceWith($('#fileopen').clone(true));
            	url = 'login';
            	$.post(url, {
            		uid : uid
            	}, function(userData) {
            		if (userData.result == 0) {
            			alert_modal("好像出现问题", data.message);
            		} else if (userData.result == 1) {
            			$('#storage').attr('aria-valuenow', userData.used_storage);
            			$('#storage').css('width', userData.used_storage + '%');
            			$('#storage_text').html('已使用' + userData.used_storage + 'MB空间');
            			var l = userData.models.length - 1;
            			var obj = $('#wrapper').clone();
            			$(obj).css('display', 'float');
            			$(obj).children('p').html(userData.models[l].model_name);
            			$(obj).children('img').attr('src', userData.models[l].thumbnail_url);
            			$(obj).children('div').children('button').attr('id', userData.models[l].model_id);
            			$(obj).children('div').children('a.export_model').attr('href', userData.models[l].model_url);
            			open_url = 'editor.html?uid=' + uid + '&mid=' + userData.models[l].model_id;
            			$(obj).children('div').children('a.open_model').attr('href', open_url);
            			$(obj).hide();
            			$('#model-middle').append(obj);
            			$(obj).children('div').children('button.delete_model').click(del_action);
            			$(obj).fadeIn();
            		}
            	}, "json");
            },
            error: function(data) {
            	alert_modal("好像出现问题", data.message);
            }
        });
		$('#importModal').modal('hide');
	});
	
	$.post(url, {
		uid : uid
	}, function(data) {
		if (data.result == 0) {
			alert_modal("好像出现问题", data.message);
		} else if (data.result == 1) {
			refreshView(data);
			$('button.delete_model').click(del_action);
		}
	}, "json");
});