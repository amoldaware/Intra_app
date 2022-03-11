package com.cgtsi.action;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.cgtsi.action.BaseAction;
import com.cgtsi.actionform.ClaimActionForm;
import com.cgtsi.claim.ClaimApplication;
import com.cgtsi.common.DatabaseException;
import com.cgtsi.common.Log;
import com.cgtsi.util.DBConnection;

public class PrincipalRepayment extends BaseAction{
	public ActionForward updateClaimApplication(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		ClaimActionForm claimForm = (ClaimActionForm) form;
		ClaimApplication claimapplication = claimForm.getClaimapplication();
//		ClaimApplication claimapplication1 = claimForm.getTcprincipal();
		
		String principalRepayment = request.getParameter("principalRepayment");
		String interestOnPrincipal = request.getParameter("interestAndOtherCharges");
		String clmRefNo = claimapplication.getClaimRefNumber();
		String cgpan = request.getParameter("cgpan");
		//String cgpan=claimapplication.
		
		System.out.println("principalRepayment:"+principalRepayment+"\n");
		
		System.out.println("interestOnPrincipal:"+interestOnPrincipal+"\n");
		System.out.println("clmRefNo:"+clmRefNo+"\n");
		System.out.println("cgpan:"+cgpan+"\n");
		
		String disbursementDate = null;
		double principalAmount = 0.0D;
		double interestAmnt = 0.0D;
		double npaAmount = 0.0D;
		double legalAmount = 0.0D;
		double claimAmount = 0.0D;
		double osAsOnSecondClmLodgemnt = 0.0D;
		String tcClaimFlag = null;
		double totalDisbAmnt = 0.0D;

		Connection connection = null;
		CallableStatement callableStmt = null;
		PreparedStatement pstmt=null;
		int status = -1;
		String errorCode = null;
		
		ResultSet rs=null;
		try {
			connection = DBConnection.getConnection();
			String selectQuery="select CLM_REF_NO,CGPAN,CTD_LAST_DISBURSEMENT_DT,CTD_NPA_OUTSTANDING_AMT,CTD_LEGAL_OUTSTANDING_AMT,"
					+ "CTD_FIRST_CLM_DT_OUTSTAND_AMT,CTD_SECOND_CLM_DT_OUTSTAND_AMT,CTD_CLM_APPLIED_FLAG,CTD_DISB_AMT from CLAIM_TC_DETAIL_TEMP where clm_ref_no=? and cgpan=?";
			pstmt=connection.prepareStatement(selectQuery);
			System.out.println("selectQuery:"+selectQuery);
			pstmt.setString(1, clmRefNo);
			pstmt.setString(2, cgpan);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				
				disbursementDate = rs.getString("CTD_LAST_DISBURSEMENT_DT");
				npaAmount = rs.getDouble("CTD_NPA_OUTSTANDING_AMT");
				legalAmount = rs.getDouble("CTD_LEGAL_OUTSTANDING_AMT");
				claimAmount = rs.getDouble("CTD_FIRST_CLM_DT_OUTSTAND_AMT");
				osAsOnSecondClmLodgemnt = rs.getDouble("CTD_SECOND_CLM_DT_OUTSTAND_AMT");
				tcClaimFlag = rs.getString("CTD_CLM_APPLIED_FLAG");
				totalDisbAmnt = rs.getDouble("CTD_DISB_AMT");

			
			callableStmt = connection
					.prepareCall("{?=call funcUpdateClaimTLDetails(?,?,?,?,?,?,?,?,?,?,?,?)}");

			callableStmt.registerOutParameter(1,
					java.sql.Types.INTEGER);
			callableStmt.setString(2, clmRefNo);
			callableStmt.setString(3, cgpan);
			callableStmt.setString(4, disbursementDate);
			callableStmt.setDouble(5, principalAmount);
			callableStmt.setDouble(6, interestAmnt);
			callableStmt.setDouble(7, npaAmount);
			callableStmt.setDouble(8, legalAmount);
			callableStmt.setDouble(9, claimAmount);
			callableStmt.setDouble(10, osAsOnSecondClmLodgemnt);
			// callableStmt.registerOutParameter(11,java.sql.Types.VARCHAR);
			callableStmt.setString(11, tcClaimFlag);
			callableStmt.setDouble(12, totalDisbAmnt);
			callableStmt.registerOutParameter(13,
					java.sql.Types.VARCHAR);

			callableStmt.execute();
			status = callableStmt.getInt(1);
			// errorCode = callableStmt.getString(11);
			errorCode = callableStmt.getString(13);
			if (status == 1) {
				Log.log(2, "CPDAO", "saveClaimApplication()",
						"SP returns a 1. Error code is :"
								+ errorCode);
				callableStmt.close();
				try {
					connection.rollback();
				} catch (SQLException sqlex) {
					throw new DatabaseException(sqlex.getMessage());
				}
				throw new DatabaseException(errorCode);
			}	return mapping.findForward("success");
			//response.sendRedirect("displayClmApplicationDtlModified.do?method=displayClmApplicationDtlModified&ClaimRefNumber="+clmRefNo+"&error");
			
			} else {
				//response.sendRedirect("displayClmApplicationDtlModified.do?method=displayClmApplicationDtlModified&ClaimRefNumber="+clmRefNo+"&success");
				return mapping.findForward("failure");
			}
		} catch (Exception e) {
			System.out.println("Exception in try catch block:" + e.getMessage());

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			if (connection != null) {
				connection.close();
			}

		}
		return mapping.findForward("success");

	}
}
