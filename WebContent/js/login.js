$(document).ready(function() {
	var alert_modal = function(title, content) {
		$('.alertTitle').html(title);
		$('.alertBody').children('p').html(content);
		$('#alertModal').modal('show');
	};

	$("#btn-register").click(function() {
		location.href = "./register.html";
		return false;
	});
	$("#btn-login").click(function() {
		username = $("#inputEmail").val();
		password = $("#inputPassword").val();
		url = "login";
		$.post(url, {
			username : username,
			password : password
		}, function(data) {
			if (data.result == 0) {
				alert_modal("好像出现问题", data.message);
			} else if (data.result == 1) {
				location.href = "./home.html?uid=" + data.uid;
			}
		}, "json");
	});

	$("#btn-forget-password").click(function() {
		registeredMail = $("#mail-find-password").val();
		url = "findpw";
		$.post(url, {
			username : registeredMail
		}, function(data) {
			if (data.result == 0) {
				alert_modal("好像出现问题", data.message);
			} else if (data.result == 1) {
				alert_modal("发送成功", data.message);
			}
		}, "json");
		$('#myModal').modal('hide');
	});
});