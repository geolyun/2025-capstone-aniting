// /static/js/logout.js
$(document).ready(function () {
  if ($("#logoutBtn").length > 0) {
    $("#logoutBtn").on("click", function () {
      $.ajax({
        type: "POST",
        url: "/api/users/logout",
        success: function (res) {
          alert(res.message);
          window.location.href = "/";
        },
        error: function () {
          alert("로그아웃 중 오류가 발생했습니다.");
        }
      });
    });
  }
});
