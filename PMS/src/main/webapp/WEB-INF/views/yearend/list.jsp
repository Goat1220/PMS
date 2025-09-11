<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="../includes/ui.jsp" %>
<%@ include file="../includes/table.jsp"%>

	<script type="text/javascript">
        document.addEventListener("DOMContentLoaded", function() {
            const columnDefs = [
                { headerName: "번호", field: "yrtId" },
                { headerName: "사원ID", field: "empId" },
                { headerName: "사업장", field: "bizPlace" },
                { headerName: "기준년도", field: "baseYear" },
                { headerName: "세금 적용 구분", field: "taxApplyType" },
                { headerName: "세금 적용 결과", field: "taxApplyResult" },
                { headerName: "확정 여부", field: "confirmYn" },
                { headerName: "생성 일시", field: "createAt" }
            ];

            const rowData = [
                <c:forEach var="item" items="${list}" varStatus="s">
                    {
                        yrtId: "<c:out value='${item.yrtId}'/>",
                        empId: "<c:out value='${item.empId}'/>",
                        bizPlace: "<c:out value='${item.bizPlace}'/>",
                        baseYear: "<c:out value='${item.baseYear}'/>",
                        taxApplyType: "<c:out value='${item.taxApplyType}'/>",
                        taxApplyResult: "<c:out value='${item.taxApplyResult}'/>",
                        confirmYn: "<c:out value='${item.confirmYn}'/>",
                        createAt: "<c:out value='${item.createAt}'/>"
                    }
                    <c:if test="${!s.last}">,</c:if>
                </c:forEach>
            ];

            initCommonGrid(columnDefs, rowData);
        });
    </script>
</body>
</html>
