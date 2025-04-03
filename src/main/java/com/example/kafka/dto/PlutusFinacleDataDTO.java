package com.example.kafka.dto;

import snehal.commonlibs.avro.PlutusFinacleData;
import java.nio.ByteBuffer;

public class PlutusFinacleDataDTO {
    private String foracid;
    private String acctname;
    private String lastTranDateCr;
    private String tranDate;
    private String tranId;
    private String partTranSrlNum;
    private String delFlg;
    private String tranType;
    private String tranSubType;
    private String partTranType;
    private String glSubHeadCode;
    private String acid;
    private String valueDate;
    private double tranAmt;
    private String tranParticular;
    private String entryDate;
    private String pstdDate;
    private String refNum;
    private String instrmntType;
    private String instrmntDate;
    private String instrmntNum;
    private String tranRmks;
    private String custId;
    private String brCode;
    private String crncyCode;
    private String tranCrncyCode;
    private double refAmt;
    private String solId;
    private String bankCode;
    private String treaRefNum;
    private String reversalDate;

    // Getters and Setters
    public String getForacid() {
        return foracid;
    }

    public void setForacid(String foracid) {
        this.foracid = foracid;
    }

    public String getAcctname() {
        return acctname;
    }

    public void setAcctname(String acctname) {
        this.acctname = acctname;
    }

    public String getLastTranDateCr() {
        return lastTranDateCr;
    }

    public void setLastTranDateCr(String lastTranDateCr) {
        this.lastTranDateCr = lastTranDateCr;
    }

    public String getTranDate() {
        return tranDate;
    }

    public void setTranDate(String tranDate) {
        this.tranDate = tranDate;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getPartTranSrlNum() {
        return partTranSrlNum;
    }

    public void setPartTranSrlNum(String partTranSrlNum) {
        this.partTranSrlNum = partTranSrlNum;
    }

    public String getDelFlg() {
        return delFlg;
    }

    public void setDelFlg(String delFlg) {
        this.delFlg = delFlg;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getTranSubType() {
        return tranSubType;
    }

    public void setTranSubType(String tranSubType) {
        this.tranSubType = tranSubType;
    }

    public String getPartTranType() {
        return partTranType;
    }

    public void setPartTranType(String partTranType) {
        this.partTranType = partTranType;
    }

    public String getGlSubHeadCode() {
        return glSubHeadCode;
    }

    public void setGlSubHeadCode(String glSubHeadCode) {
        this.glSubHeadCode = glSubHeadCode;
    }

    public String getAcid() {
        return acid;
    }

    public void setAcid(String acid) {
        this.acid = acid;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public double getTranAmt() {
        return tranAmt;
    }

    public void setTranAmt(double tranAmt) {
        this.tranAmt = tranAmt;
    }

    public String getTranParticular() {
        return tranParticular;
    }

    public void setTranParticular(String tranParticular) {
        this.tranParticular = tranParticular;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getPstdDate() {
        return pstdDate;
    }

    public void setPstdDate(String pstdDate) {
        this.pstdDate = pstdDate;
    }

    public String getRefNum() {
        return refNum;
    }

    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    public String getInstrmntType() {
        return instrmntType;
    }

    public void setInstrmntType(String instrmntType) {
        this.instrmntType = instrmntType;
    }

    public String getInstrmntDate() {
        return instrmntDate;
    }

    public void setInstrmntDate(String instrmntDate) {
        this.instrmntDate = instrmntDate;
    }

    public String getInstrmntNum() {
        return instrmntNum;
    }

    public void setInstrmntNum(String instrmntNum) {
        this.instrmntNum = instrmntNum;
    }

    public String getTranRmks() {
        return tranRmks;
    }

    public void setTranRmks(String tranRmks) {
        this.tranRmks = tranRmks;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getBrCode() {
        return brCode;
    }

    public void setBrCode(String brCode) {
        this.brCode = brCode;
    }

    public String getCrncyCode() {
        return crncyCode;
    }

    public void setCrncyCode(String crncyCode) {
        this.crncyCode = crncyCode;
    }

    public String getTranCrncyCode() {
        return tranCrncyCode;
    }

    public void setTranCrncyCode(String tranCrncyCode) {
        this.tranCrncyCode = tranCrncyCode;
    }

    public double getRefAmt() {
        return refAmt;
    }

    public void setRefAmt(double refAmt) {
        this.refAmt = refAmt;
    }

    public String getSolId() {
        return solId;
    }

    public void setSolId(String solId) {
        this.solId = solId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getTreaRefNum() {
        return treaRefNum;
    }

    public void setTreaRefNum(String treaRefNum) {
        this.treaRefNum = treaRefNum;
    }

    public String getReversalDate() {
        return reversalDate;
    }

    public void setReversalDate(String reversalDate) {
        this.reversalDate = reversalDate;
    }

    // Convert from Avro to DTO
    public static PlutusFinacleDataDTO fromAvro(PlutusFinacleData avro) {
        PlutusFinacleDataDTO dto = new PlutusFinacleDataDTO();
        dto.setForacid(avro.getFORACID());
        dto.setAcctname(avro.getACCTNAME());
        dto.setLastTranDateCr(avro.getLASTTRANDATECR());
        dto.setTranDate(avro.getTRANDATE());
        dto.setTranId(avro.getTRANID());
        dto.setPartTranSrlNum(avro.getPARTTRANSRLNUM());
        dto.setDelFlg(avro.getDELFLG());
        dto.setTranType(avro.getTRANTYPE());
        dto.setTranSubType(avro.getTRANSUBTYPE());
        dto.setPartTranType(avro.getPARTTRANTYPE());
        dto.setGlSubHeadCode(avro.getGLSUBHEADCODE());
        dto.setAcid(avro.getACID());
        dto.setValueDate(avro.getVALUEDATE());
        dto.setTranAmt(avro.getTRANAMT());
        dto.setTranParticular(avro.getTRANPARTICULAR());
        dto.setEntryDate(avro.getENTRYDATE());
        dto.setPstdDate(avro.getPSTDDATE());
        dto.setRefNum(avro.getREFNUM());
        dto.setInstrmntType(avro.getINSTRMNTTYPE());
        dto.setInstrmntDate(avro.getINSTRMNTDATE());
        dto.setInstrmntNum(avro.getINSTRMNTNUM());
        dto.setTranRmks(avro.getTRANRMKS());
        dto.setCustId(avro.getCUSTID());
        dto.setBrCode(avro.getBRCODE());
        dto.setCrncyCode(avro.getCRNCYCODE());
        dto.setTranCrncyCode(avro.getTRANCRNCYCODE());
        dto.setRefAmt(avro.getREFAMT());
        dto.setSolId(avro.getSOLID());
        dto.setBankCode(avro.getBANKCODE());
        dto.setTreaRefNum(avro.getTREAREFNUM());
        dto.setReversalDate(avro.getREVERSALDATE());
        return dto;
    }

    // Convert from DTO to Avro
    public PlutusFinacleData toAvro() {
        return PlutusFinacleData.newBuilder()
                .setFORACID(foracid)
                .setACCTNAME(acctname)
                .setLASTTRANDATECR(lastTranDateCr)
                .setTRANDATE(tranDate)
                .setTRANID(tranId)
                .setPARTTRANSRLNUM(partTranSrlNum)
                .setDELFLG(delFlg)
                .setTRANTYPE(tranType)
                .setTRANSUBTYPE(tranSubType)
                .setPARTTRANTYPE(partTranType)
                .setGLSUBHEADCODE(glSubHeadCode)
                .setACID(acid)
                .setVALUEDATE(valueDate)
                .setTRANAMT(tranAmt)
                .setTRANPARTICULAR(tranParticular)
                .setENTRYDATE(entryDate)
                .setPSTDDATE(pstdDate)
                .setREFNUM(refNum)
                .setINSTRMNTTYPE(instrmntType)
                .setINSTRMNTDATE(instrmntDate)
                .setINSTRMNTNUM(instrmntNum)
                .setTRANRMKS(tranRmks)
                .setCUSTID(custId)
                .setBRCODE(brCode)
                .setCRNCYCODE(crncyCode)
                .setTRANCRNCYCODE(tranCrncyCode)
                .setREFAMT(refAmt)
                .setSOLID(solId)
                .setBANKCODE(bankCode)
                .setTREAREFNUM(treaRefNum)
                .setREVERSALDATE(reversalDate)
                .build();
    }
} 