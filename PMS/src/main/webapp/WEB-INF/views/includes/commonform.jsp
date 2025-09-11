<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<style>
  :root{--gap:12px;--radius:10px;--border:#e5e7eb;--bg:#f8fafc;--text:#111827;--muted:#6b7280;--total:#eaf7ea}
  *{box-sizing:border-box}
  body{margin:0;font-family:system-ui,-apple-system,"Segoe UI",Roboto,"Noto Sans KR","Apple SD Gothic Neo",Arial,"맑은 고딕",sans-serif;color:var(--text);background:var(--bg)}
  header{padding:16px 20px;background:#fff;border-bottom:1px solid var(--border);position:sticky;top:0;z-index:5}
  .container{padding:16px 20px}
  .row{display:grid;grid-template-columns:2fr 1fr;gap:var(--gap);align-items:start}
  .card{background:#fff;border:1px solid var(--border);border-radius:var(--radius);overflow:hidden}
  .card>.card-header{padding:12px 14px;border-bottom:1px solid var(--border);font-weight:600}
  .card>.card-body{padding:12px 14px}
  .filters{display:flex;gap:8px;align-items:center;flex-wrap:wrap}
  .filters input[type="month"],.filters select,.filters input[type="text"]{padding:8px 10px;border:1px solid var(--border);border-radius:8px}
  .filters button{padding:8px 12px;border:1px solid #0ea5e9;background:#0ea5e9;color:#fff;border-radius:8px;cursor:pointer}
  .filters button.secondary{background:#fff;color:#0ea5e9}
  .container button{padding:8px 12px;border:1px solid #0ea5e9;background:#0ea5e9;color:#fff;border-radius:8px;cursor:pointer}
  .container button.secondary{background:#fff;color:#0ea5e9}
  .table-wrap{overflow:auto;border:1px solid var(--border);border-radius:8px}
  table{border-collapse:collapse;width:100%;min-width:1600px}
  thead th{position:sticky;top:0;background:#f1f5f9;border-bottom:1px solid var(--border);font-weight:600;white-space:nowrap}
  th,td{border-bottom:1px solid var(--border);padding:8px 10px;text-align:left;font-size:13px}
  tbody tr{cursor:pointer}
  tbody tr:hover{background:#f8fafc}
  tbody tr.active{background:#e0f2fe}
  .muted{color:var(--muted)}
  .grid-2{display:grid;grid-template-columns:1fr;gap:var(--gap)}
  .list{border:1px solid var(--border);border-radius:8px;overflow:hidden}
  .pill{display:inline-block;padding:2px 8px;font-size:12px;border-radius:999px;border:1px solid var(--border);background:#f8fafc}
  .right-top{display:flex;justify-content:space-between;align-items:center;gap:6px}
  .ghost{opacity:.5}
  .row-total{background:var(--total)!important;font-weight:600}
  .right-tables table{min-width:100%}
</style>