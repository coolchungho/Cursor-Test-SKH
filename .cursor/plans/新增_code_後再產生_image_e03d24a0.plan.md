---
name: 新增 code 後再產生 image
overview: 說明目前 docs 截圖的用途與兩種產生方式：手動截圖（現有做法）與可選的自動化截圖腳本（Playwright）。
todos: []
isProject: false
---

# 新增 code 後再產生 docs 截圖的作法

## 現況

- [README.md](c:\Users\chungho\OneDrive\Cursor\Cursor Test SKH\README.md) 中引用四張示意圖，放在 `docs/`：
  - `docs/case-search.png` — 個案篩選頁
  - `docs/intake-list.png` — 收案清單頁
  - `docs/case-profile.png` — 個案資料管理頁
  - `docs/visit-timeline.png` — 訪視追蹤頁
- 這些圖片是**手動截圖**，專案內沒有自動產生腳本（[package.json](c:\Users\chungho\OneDrive\Cursor\Cursor Test SKH\package.json) 僅有 `dev` / `build` / `preview` / `lint`）。

---

## 方式一：手動重新截圖（建議先做）

改完 code 後若要更新 docs 圖片，可依下列步驟：

1. **啟動應用**

```bash
   npm run dev
   

```

   瀏覽器開啟 `http://localhost:5173`。

1. **依序開啟四個頁面並截圖**
  - **個案篩選**：`/cases/search` → 截圖整頁或主內容區 → 另存為 `docs/case-search.png`
  - **收案清單**：`/cases/intake`（先從搜尋頁加一兩個個案到收案清單較好看）→ 存為 `docs/intake-list.png`
  - **個案資料**：從搜尋或收案清單點「個案資料」進入 `/cases/:patientId/profile` → 存為 `docs/case-profile.png`
  - **訪視追蹤**：從收案清單點「訪視追蹤」進入 `/cases/:patientId/visits` → 存為 `docs/visit-timeline.png`
2. **截圖方式（擇一）**
  - Windows：`Win + Shift + S` 或「剪取工具」選取區域後另存
  - 瀏覽器：F12 → 開發者工具右上選單 → "Capture node screenshot" 可只截單一區塊；或用擴充功能做整頁截圖
  - 存檔時直接覆蓋 `docs/` 下對應檔名即可，README 的 `![](docs/xxx.png)` 會自動顯示新圖。

---

## 方式二：自動化截圖（可選）

若希望「改完 code 後一鍵更新四張圖」，可新增以 **Playwright** 為主的腳本：

- **流程概念**
  1. 啟動或連到已運行的 dev server（例如 `http://localhost:5173`）。
  2. 用 Playwright 依序造訪四個路由，每頁等待內容載入（例如等表格或卡片出現）。
  3. 對整頁或指定區塊呼叫 `page.screenshot()`，寫入 `docs/case-search.png`、`docs/intake-list.png`、`docs/case-profile.png`、`docs/visit-timeline.png`。
- **實作要點**
  - 新增 devDependency：`playwright`。
  - 新增腳本檔（例如 `scripts/capture-docs-screenshots.ts` 或 `.mjs`）：啟動 browser → 依序 `page.goto(url)` → `page.screenshot({ path: 'docs/xxx.png' })`。
  - 收案清單、個案資料、訪視追蹤頁需要「有資料」才好看：可先導向搜尋頁、用 Playwright 操作加入收案、再導向各頁截圖；或使用固定測試用 `patientId`（若 HAPI 有已知 ID）。
  - 在 `package.json` 加一則 script，例如：`"screenshots": "node scripts/capture-docs-screenshots.mjs"`，執行前需先 `npm run dev` 在背景跑，或由腳本內用 `vite preview` 或 `child_process` 啟動再截圖。
- **取捨**
  - 優點：改版後執行一次即可更新四張圖，格式一致。
  - 缺點：需處理 FHIR 資料與路由（沒資料時畫面可能空白）、需維護腳本與選擇器，且本專案目前無 E2E 環境，需從頭設定。

---

## 建議

- **短期**：改 code 後用**方式一**手動截圖覆蓋 `docs/*.png` 即可。
- **若常改 UI 且希望文件圖常保最新**：再考慮引入**方式二**，並把「先讓收案/個案/訪視頁有資料」的流程寫進腳本或 README（例如先手動加一筆收案再跑腳本，或腳本內用固定 ID）。

若你決定要做方式二，可再指定「用哪個 patientId、是否要腳本自動加收案」，我可以幫你寫出具體腳本與 `package.json` 修改範例。