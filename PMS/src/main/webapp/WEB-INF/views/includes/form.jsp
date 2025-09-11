<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- 공용 버튼 폼 -->
<%-- 버튼의 공통 스타일만 정의 --%>
<style>
  .common-btn {
    padding: 8px 16px;
    margin: 4px;
    border: none;
    border-radius: 4px;
    background-color: #4CAF50;
    color: white;
    cursor: pointer;
  }
  .common-btn:hover {
    background-color: #45a049;
  }
</style>

<%-- 버튼 생성 함수 제공 (문구, 함수 이름만 바꾸면 됨) --%>
<script>
  /**
   * 공용 버튼 생성 함수
   * @param {string} label - 버튼에 표시될 텍스트
   * @param {string} onClickFn - 버튼 클릭 시 실행할 함수 이름
   * @returns {HTMLButtonElement} 버튼 DOM
   */
  function createCommonButton(label, onClickFn) {
    const btn = document.createElement("button");
    btn.className = "common-btn";
    btn.textContent = label;
    btn.setAttribute("onclick", onClickFn + "()");
    return btn;
  }
</script>