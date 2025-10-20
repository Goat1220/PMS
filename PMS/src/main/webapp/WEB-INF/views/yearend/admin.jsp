<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="../includes/commonform.jsp"%>
<%@ include file="../includes/table.jsp"%>

<html>
<head>
<title>年末調整処理・申告</title>
<style>
/* (스타일은 동일, 생략 가능) */
</style>
</head>
<body>

	<header>
		<h2>年末調整処理・申告</h2>
	</header>

	<div class="container">

		<!-- 検索バー -->
		<div class="filters" style="margin: 10px 0 12px">
			<!-- 1行目 -->
			<div
				style="display: flex; gap: 6px; align-items: center; flex-wrap: wrap; width: 100%">
				<label><b>調整年度</b></label> <input id="searchYear" type="text"
					value="<%-- ${empty cond.baseYear ? '2018' : cond.baseYear} --%>2025"> <label><b>調整事業所</b></label>
				<select id="searchBizPlace">
					<option value="">全体</option>
					<option value="本社" ${cond.bizPlace=='本社' ? 'selected' : ''}>本社</option>
				</select>

				<button id="btnAllSettle">対象者一括調整処理</button>
				<button id="btnQuery">照会</button>
			</div>

			<!-- 2行目 -->
			<div
				style="display: flex; gap: 6px; align-items: center; flex-wrap: wrap; width: 100%; margin-top: 6px">
				<label>部署</label> <input id="searchDept" type="text"
					value="${cond.deptName}"> <label>社員</label> <input
					id="searchEmp" type="text" value="${cond.empName}">

				<button id="btnSettle">調整処理</button>
				<button class="secondary" id="btnUnsettle">調整結果削除</button>
				<button class="secondary" id="btnPenalty">納付特例税額反映</button>
				<button class="secondary"
					onclick="exportTableToExcel('#adminTable','年末調整_処理一覧.csv')">エクセル</button>
			</div>
		</div>

		<div class="layout">
			<!-- ============ 左側：一覧テーブル ============ -->
			<div class="card">
				<div class="card-body left-body">
					<div class="left-wrap">
						<table id="adminTable">
							<thead>
								<tr>
									<th class="col-select">選択</th>
									<th class="col-file">ファイル作成</th>
									<th class="col-name">氏名</th>
									<th class="col-no">社員番号</th>
									<th class="col-dept">部署</th>
									<th class="col-biz">調整事業所</th>
									<th class="col-drop">税額区分</th>
									<th class="col-chk">調整処理</th>
									<th class="col-chk">納付特例<br>税額反映
									</th>
									<th class="col-chk">居住者<br>区分
									</th>
									<th class="col-chk">担当者<br>締め
									</th>
									<th class="col-result">税額区分<br>結果
									</th>
									<th class="col-chk">個人<br>締め
									</th>
									<th class="col-chk">確定</th>
									<th class="col-chk">認定控除<br>反映
									</th>
									<th class="col-chk">調整申告<br>除外
									</th>
									<th class="col-chk">健康保険<br>PDF反映
									</th>
									<th class="col-chk">国民年金<br>PDF反映
									</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="h" items="${list}">
									<tr data-yrt-id="${h.yrtId}">
										<td class="col-select"><input type="checkbox"
											data-col="select"></td>
										<td class="col-file"><input type="checkbox"
											data-col="file"></td>

										<td class="col-name">${h.empName}</td>
										<td class="col-no">${h.empNo}</td>
										<td class="col-dept">${h.deptName}</td>
										<td class="col-biz">${h.bizPlace}</td>

										<td class="col-drop"><select data-col="applyType">
												<option value="基本(比較)" selected>基本(比較)</option>
												<option value="一般(簡易税額)">一般(簡易税額)</option>
												<option value="標準税額控除">標準税額控除</option>
												<option value="単一税率">単一税率</option>
												<option value="単一税率-分離課税">単一税率-分離課税</option>
												<option value="簡易税額-基礎控除なし">簡易税額-基礎控除なし</option>
												<option value="累進控除なし">累進控除なし</option>
												<option value="任意税率">任意税率</option>
												<option value="強制調整">強制調整</option>
										</select></td>

										<td class="col-chk"><input type="checkbox"
											data-col="settle" disabled></td>
										<td class="col-chk"><input type="checkbox"
											data-col="penalty" disabled></td>

										<td class="col-chk"><input type="checkbox"
											data-col="resident" checked disabled></td>
										<td class="col-chk"><input type="checkbox"
											data-col="staffClosed"></td>

										<td class="col-result"><input type="text" value="標準税額控除"
											readonly
											style="width: 100%; border: none; background: transparent; text-align: center;">
										</td>

										<td class="col-chk"><input type="checkbox"
											data-col="personalClosed" checked disabled></td>
										<td class="col-chk"><input type="checkbox"
											data-col="confirm"></td>
										<td class="col-chk"><input type="checkbox"
											data-col="allowanceApplied" disabled></td>
										<td class="col-chk"><input type="checkbox"
											data-col="excludeSubmit"></td>
										<td class="col-chk"><input type="checkbox"
											data-col="hiPdf" disabled></td>
										<td class="col-chk"><input type="checkbox"
											data-col="npPdf" disabled></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>

					<script>
          enableSort('#adminTable');       // ヘッダークリックでソート
          enablePaging('#adminTable', 20); // 20行ずつページング
        </script>
				</div>
			</div>
		</div>
	</div>

	<script>
  /* 選択された行を取得 */
   function getSelectedRows(){
    const rows=[];
    document.querySelectorAll('#adminTable tbody tr').forEach(tr=>{
      const cb=tr.querySelector('input[type=checkbox][data-col="select"]');
      if(cb && cb.checked) rows.push(tr);
    });
    return rows;
  } 

  /* 上部ボタン動作 */
document.getElementById('btnAllSettle').addEventListener('click', ()=>{
  const rows = document.querySelectorAll('#adminTable tbody tr');
  if(!rows.length){ alert('処理するデータがありません。'); return; }

  rows.forEach(tr=>{
    const cb = tr.querySelector('input[type=checkbox][data-col="settle"]');
    if(cb) cb.checked = true;
    tr.classList.add('active');
    setTimeout(()=>tr.classList.remove('active'),300);
  });
});
  document.getElementById('btnSettle').addEventListener('click', ()=>{
    const rows=getSelectedRows(); if(!rows.length){alert('選択された社員がありません。'); return;}
    rows.forEach(tr=>{
      tr.querySelector('input[data-col="settle"]').checked=true;
      tr.classList.add('active'); setTimeout(()=>tr.classList.remove('active'),300);
    });
  });
  document.getElementById('btnUnsettle').addEventListener('click', ()=>{
    const rows=getSelectedRows(); if(!rows.length){alert('選択された社員がありません。'); return;}
    rows.forEach(tr=>{
      tr.querySelector('input[data-col="settle"]').checked=false;
      tr.classList.add('active'); setTimeout(()=>tr.classList.remove('active'),300);
    });
  }); 
  document.getElementById('btnPenalty').addEventListener('click', ()=>{
    const rows=getSelectedRows(); if(!rows.length){alert('選択された社員がありません。'); return;}
    rows.forEach(tr=>{
      tr.querySelector('input[data-col="penalty"]').checked=true;
      tr.classList.add('active'); setTimeout(()=>tr.classList.remove('active'),300);
    });
  });

  /* 照会ボタン → パラメータを構成して移動 */
  document.getElementById('btnQuery').addEventListener('click', ()=>{
    const year=document.getElementById('searchYear').value;
    const biz =document.getElementById('searchBizPlace').value;
    const dept=document.getElementById('searchDept').value;
    const emp =document.getElementById('searchEmp').value;

    const q=new URLSearchParams();
    if(year) q.append('baseYear',year);
    if(biz)  q.append('bizPlace',biz);
    if(dept) q.append('deptName',dept);
    if(emp)  q.append('empName',emp);

    location.href='/yearend/admin/list?'+q.toString();
  });
</script>

</body>
</html>
