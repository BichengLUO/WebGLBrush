function preview() {
	if (event.target.files.length === 0)
		return;
	var file = event.target.files[0];
	if (!file.type.match('image.*'))
		return;
	var reader = new FileReader();
	reader.onload = function (evt)
	{
		$('#img-avatar').attr('src', evt.target.result);
	};
	reader.readAsDataURL(file);
}


$(document).ready(function() {
	var options = {
		animation: true,
		placement: "top",
		content: "点击上传头像",
		trigger: "hover"
	};
	var alert_modal = function(title, content) {
		$('.alertTitle').html(title);
		$('.alertBody').children('p').html(content);
		$('#alertModal').modal('show');
	};
	$("#img-avatar").popover(options);

	$("#btn-register").click(function() {
		username = $("#inputEmail3").val();
		password = $("#inputPassword3").val();
		confirmPass = $("#inputPasswordAgain3").val();
		nickname = $("#inputNickname3").val();

		var errorInfo = function(info) {
			var options = {
				animation: true,
				placement: "bottom",
				content: info,
				trigger: "focus"
			};
			return options;
		};

		var checkPassword = function(pass) {
			if (pass == "") {
				$("#inputPassword3").popover(errorInfo("密码不为空!"));
				$("#inputPassword3").focus();
				return false;

			}
			if (pass.length < 4 || pass.length > 16) {
				$("#inputPassword3").popover(errorInfo("密码长度4-16位!"));
				$("#inputPassword3").focus();
				return false;

			}
			return true;
		};

		var checkConfirmPassword = function(confirmPass) {
			if (confirmPass == "") {
				$("#inputPasswordAgain3").popover(errorInfo("密码不为空!"));
				$("#inputPasswordAgain3").focus();
				return false;
			}
			if (confirmPass != password) {
				$("#inputPasswordAgain3").popover(errorInfo("输入密码和确认密码不一致!"));
				$("#inputPasswordAgain3").focus();
				return false;
			}
			return true;
		};

		var checkNickname = function(nick) {
			if (nick == "") {
				$("#inputNickname3").popover(errorInfo("昵称不为空!"));
				$("#inputNickname3").focus();
				return false;
			}
			if (nick.length < 4 || nick.length > 16) {
				$("#inputNickname3").popover(errorInfo("姓名输入的长度4-16个字符!"));
				$("#inputNickname3").focus();
				return false;
			}
			name = nick.toLowerCase();
			for (var i = 0; i < name.length; i++) {
				var charname = name.charAt(i);
				if (!(charname >= 0 && charname <= 9) && (!(charname >= 'a' && charname <= 'z')) && (charname != '_')) {
					$("#inputNickname3").popover(errorInfo("姓名包含非法字母，只能包含字母，数字，和下划线!"));
					$("#inputNickname3").focus();
					return false;
				}
			}
			return true;

		};

		var check = function() {
			if (checkPassword(password) && checkConfirmPassword(confirmPass) && checkNickname(nickname)) {
				var form_data = new FormData();
				if ($("#file-upload").prop("files").length > 0) {
					var file_data = $("#file-upload").prop("files")[0];
					form_data.append("file", file_data);
				}
				form_data.append("username", username);
				form_data.append("password", password);
				form_data.append("nickname", nickname);
				$.ajax({
			                url: "register",
			                dataType: 'json',
			                cache: false,
			                contentType: false,
			                processData: false,
			                data: form_data,
			                type: 'post',
			                success: function(data) {
			                	$('#file-upload').replaceWith($('#file-upload').clone(true));
			                	console.log(data.message);
			                	alert_modal("注册成功", data.message);
			                },
			                error: function(data) {
			                	alert_modal("好像出现问题", data.message);
			                }
			     });
				return true;
			} else {
				return false;
			}

		};
		check();
		return false;
	});

	function openBrowse() {
		var ie = navigator.appName == "Microsoft Internet Explorer" ? true : false;
		if (ie) {
			document.getElementById("file-upload").click();
		} else {
			var mouseEvent = document.createEvent("MouseEvents");
			mouseEvent.initEvent("click", true, true);
			document.getElementById("file-upload").dispatchEvent(mouseEvent);
		}
	}

	$("#img-avatar").click(function() {
		openBrowse();
		return true;
	});

});