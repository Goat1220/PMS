<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<style>
:root { 
	--gap: 12px; 
	--radius: 10px; 
	--border: #e5e7eb; 
	--bg: #f8fafc; 
	--text: #111827; 
	--muted: #6b7280; 
	--total: #eaf7ea
}

* {
	box-sizing: border-box
}

body {
	margin: 0;
	font-family: system-ui, -apple-system, "Segoe UI", Roboto,
		"Noto Sans KR", "Apple SD Gothic Neo", Arial, "맑은 고딕", sans-serif;
	color: var(--text);
	background: var(--bg)
}

header {
	padding: 16px 20px;
	background: #fff;
	border-bottom: 1px solid var(--border);
	position: sticky;
	top: 0;
	z-index: 5
}

.container {
	padding: 16px 20px
}

.row {
	display: grid;
	grid-template-columns: 2fr 1fr;
	gap: var(- -gap);
	align-items: start
}

.card {
	background: #fff;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	overflow: hidden
}

.card>.card-header {
	padding: 12px 14px;
	border-bottom: 1px solid var(--border);
	font-weight: 600
}

.card>.card-body {
	padding: 12px 14px
}

.filters {
	display: flex;
	gap: 8px;
	align-items: center;
	flex-wrap: wrap
}

.filters input[type="month"], .filters select, .filters input[type="text"]
	{
	padding: 8px 10px;
	border: 1px solid var(--border);
	border-radius: 8px
}

button {
	padding: 8px 12px;
	border: 1px solid #0ea5e9;
	background: #0ea5e9 !important;
	color: #fff !important;
	border-radius: 8px;
	cursor: pointer;
	border-color: #0ea5e9 !important;
}

button.secondary {
	background: #fff;
	color: #0ea5e9
}

.table-wrap {
	overflow: auto;
	border: 1px solid var(--border);
	border-radius: 8px;
	flex: 1;
	overflow-y: auto
}

table {
  border-collapse: collapse; /* ← 이미 있음, 그대로 유지 */
  width: auto;
  table-layout: auto;
  border: 1px solid var(--border); /* 테이블 외곽선 */
}

th, td {
  border: 1px solid var(--border); /* ← 셀마다 테두리 */
  padding: 8px 10px;
  text-align: left;
  font-size: 13px;
  white-space: nowrap;
}

thead th {
  background: #f1f5f9;
  font-weight: 600;
  white-space: nowrap;
}

tbody tr {
	cursor: pointer
}

tbody tr:hover {
	background: #f8fafc
}

tbody tr.active {
	background: #e0f2fe
}

.muted {
	color: var(--muted)
}

.grid-2 {
	display: grid;
	grid-template-columns: 1fr;
	gap: var(--gap)
}

.list {
	border: 1px solid var(--border);
	border-radius: 8px;
	overflow: hidden
}

.pill {
	display: inline-block;
	padding: 2px 8px;
	font-size: 12px;
	border-radius: 999px;
	border: 1px solid var(--border);
	background: #f8fafc
}

.right-top {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 6px
}

.ghost {
	opacity: .5
}

.row-total {
	background: var(--total) !important;
	font-weight: 600
}

.right-tables table {
	min-width: 100%
}

.card-header+* {
	margin-top: 0 !important;
}

.card .title ~ * {
	margin-top: 0 !important;
}

.card.right-top {
	display: block !important;
}

/* 支給年月 입력 꾸미기 */
#yyyymm {
  /* 기본 크기/타이포 */
  width: 140px;              /* 필요 시 120~160px로 조정 */
  height: 34px;
  padding: 0 36px 0 10px;    /* 아이콘 자리 확보 위해 우측 여백 */
  font-size: 14px;
  line-height: 1.2;

  /* 테두리/배경 */
  color: #0f172a;
  background-color: #fff;

  border: 1px solid #d1d5db;      /* gray-300 */
  border-radius: 8px;
  outline: none;

  /* 달력 아이콘 (SVG data URI) */
  background-image: url("data:image/svg+xml,%3Csvg width='18' height='18' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Crect x='3' y='4' width='18' height='17' rx='2' ry='2' stroke='%236b7280' stroke-width='1.5'/%3E%3Cpath d='M8 2v4M16 2v4' stroke='%236b7280' stroke-width='1.5' stroke-linecap='round'/%3E%3Cpath d='M3 9.5h18' stroke='%236b7280' stroke-width='1.5'/%3E%3Crect x='7' y='12' width='3' height='3' rx='0.75' fill='%2393c5fd'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 18px;

  /* iOS/사파리 둥근 입력 과한 그림자 방지 */
  -webkit-appearance: none;
  appearance: none;

  transition: border-color .15s ease, box-shadow .15s ease, background-color .15s ease;
}

/* 포커스 시 강조 */
#yyyymm:focus {
  border-color: #60a5fa;            /* blue-400 */
  box-shadow: 0 0 0 3px rgba(96,165,250,.25);
}

/* 비활성화 스타일 */
#yyyymm:disabled,
#yyyymm[readonly] {
  background-color: #f3f4f6;        /* gray-100 */
  color: #6b7280;                   /* gray-500 */
  cursor: not-allowed;
}

/* placeholder 톤 다운 */
#yyyymm::placeholder {
  color: #9ca3af;                   /* gray-400 */
}

/* 부모 라벨과 간격 조정 (현재 마크업 유지) */
.filters label > #yyyymm {
  margin-left: 6px;
}

/* 컴팩트 화면에서 살짝 줄임 */
@media (max-width: 480px) {
  #yyyymm { width: 120px; }
}
</style>