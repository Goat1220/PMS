<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- table.jsp (include file) --%>
<script>
  // 엑셀 내보내기: 단순히 HTML table → xlsx 다운로드
  function exportTableToExcel(tableId, fileName="table.xlsx"){
    const table = document.querySelector(tableId);
    if(!table) return;

    let csv = [];
    const rows = table.querySelectorAll("tr");
    rows.forEach(row=>{
      let cols = row.querySelectorAll("th, td");
      let rowData = [];
      cols.forEach(col=>{
        // 콤마/줄바꿈 제거
        rowData.push(col.innerText.replace(/,/g,"").replace(/\n/g," "));
      });
      csv.push(rowData.join(","));
    });

    // Blob 다운로드
    const blob = new Blob(["\ufeff"+csv.join("\n")], {type:"text/csv;charset=utf-8;"});
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = fileName;
    link.click();
  }

  // 간단 정렬 (헤더 클릭 시)
  function enableSort(tableId){
    const table = document.querySelector(tableId);
    const headers = table.querySelectorAll("th");
    headers.forEach((th, idx)=>{
      th.style.cursor="pointer";
      th.addEventListener("click", ()=>{
        const rows = Array.from(table.querySelectorAll("tbody tr"));
        const asc = th.dataset.asc === "true" ? false : true;
        rows.sort((a,b)=>{
          const A = a.children[idx].innerText;
          const B = b.children[idx].innerText;
          return asc ? A.localeCompare(B, 'ko-KR', {numeric:true}) : B.localeCompare(A, 'ko-KR', {numeric:true});
        });
        th.dataset.asc = asc;
        rows.forEach(r=>table.querySelector("tbody").appendChild(r));
      });
    });
  }

  // 간단 필터 (input에 입력하면 tbody 검색)
  function enableFilter(inputId, tableId){
    const input = document.querySelector(inputId);
    const table = document.querySelector(tableId);
    input.addEventListener("keyup", ()=>{
      const keyword = input.value.toLowerCase();
      table.querySelectorAll("tbody tr").forEach(row=>{
        const text = row.innerText.toLowerCase();
        row.style.display = text.includes(keyword) ? "" : "none";
      });
    });
  }

  // 간단 페이징
  function enablePaging(tableId, pageSize=20){
    const table = document.querySelector(tableId);
    const tbody = table.querySelector("tbody");
    let rows = Array.from(tbody.querySelectorAll("tr"));
    let currentPage = 1;
    const totalPage = Math.ceil(rows.length / pageSize);

    function render(){
      rows.forEach((row, idx)=>{
        row.style.display = (idx >= (currentPage-1)*pageSize && idx < currentPage*pageSize) ? "" : "none";
      });
    }

    // 페이지 버튼 추가
    const pager = document.createElement("div");
    pager.style.marginTop="8px";
    for(let i=1;i<=totalPage;i++){
      const btn=document.createElement("button");
      btn.innerText=i;
      btn.addEventListener("click",()=>{ currentPage=i; render(); });
      pager.appendChild(btn);
    }
    table.parentNode.appendChild(pager);

    render();
  }
</script>
