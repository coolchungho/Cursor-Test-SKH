/** 個案列表每列擴充的本地/衍生欄位（POC 階段以 mock 或本地儲存為主） */
export interface CaseRowMeta {
  caseCategory?: string;
  hospitalizationStatus?: string;
  intakeStatus?: string;
  replyStatus?: string;
  ward?: string;
  bed?: string;
  diagnosis?: string;
  admissionDate?: string;
  dischargeDate?: string;
  attendingPhysician?: string;
  fixReason?: string;
  replyNote?: string;
  keyObservations?: string;
  lastUpdated?: string;
}
