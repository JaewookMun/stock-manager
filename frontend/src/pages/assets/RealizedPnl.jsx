import { useState, useEffect } from 'react';
import { useGetAccounts } from '../../api/accounts';
import { useGetRealizedPnl } from '../../api/realizedPnl';

const formatKRW = (value) =>
  value != null ? value.toLocaleString('ko-KR') : '-';

const formatRate = (value) => {
  if (value == null) return '-';
  const sign = value > 0 ? '+' : '';
  return `${sign}${value.toFixed(2)}%`;
};

const pnlClass = (value) => {
  if (value > 0) return 'text-danger';
  if (value < 0) return 'text-primary';
  return '';
};

const COLUMNS = [
  { label: '일시',       key: 'date',           align: 'start', render: (v) => v ?? '-' },
  { label: '종목명',     key: 'stockName',       align: 'start', render: (v) => v ?? '-' },
  { label: '실현손익',   key: 'realizedPnl',     align: 'end',   render: formatKRW, colorize: true },
  { label: '수익률',     key: 'pnlRate',      align: 'end',   render: formatRate, colorize: true },
  { label: '수량',       key: 'quantity',        align: 'end',   render: formatKRW },
  { label: '매입가',     key: 'buyPrice',   align: 'end',   render: formatKRW },
  { label: '매도체결가', key: 'executionPrice',       align: 'end',   render: formatKRW },
  { label: '수수료',     key: 'tradingCommission',      align: 'end',   render: formatKRW },
  { label: '세금',       key: 'tradingTax',             align: 'end',   render: formatKRW },
  { label: '매입금액',   key: 'purchaseAmount',  align: 'end',   render: formatKRW, getValue: (row) => row.buyPrice != null && row.quantity != null ? row.buyPrice * row.quantity : null },
  { label: '매도금액',   key: 'sellAmount',      align: 'end',   render: formatKRW, getValue: (row) => row.executionPrice != null && row.quantity != null ? row.executionPrice * row.quantity : null },
];

const PERIOD_PRESETS = [
  { label: '1개월', months: 1 },
  { label: '3개월', months: 3 },
  { label: '6개월', months: 6 },
  { label: '1년',   months: 12 },
  { label: '2년',   months: 24 },
];

const toDateString = (date) => date.toISOString().slice(0, 10);

const subtractMonths = (months) => {
  const date = new Date();
  date.setMonth(date.getMonth() - months);
  return toDateString(date);
};

const makeInitialForm = () => ({
  accountId: '',
  startDate: subtractMonths(1),
  endDate: toDateString(new Date()),
});

export default function RealizedPnl() {
  const [form, setForm] = useState(makeInitialForm);
  const [query, setQuery] = useState(null);
  const [activePreset, setActivePreset] = useState(1);

  const { accounts, accountsLoading } = useGetAccounts();
  const { realizedPnl, realizedPnlLoading, realizedPnlError } = useGetRealizedPnl(query);

  useEffect(() => {
    if (accounts.length > 0 && form.accountId === '') {
      setForm((prev) => ({ ...prev, accountId: accounts[0].id }));
    }
  }, [accounts]);

  const isFormValid = form.accountId !== '' && form.startDate && form.endDate;
  const isCustom = activePreset === 'custom';

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handlePreset = (months) => {
    setActivePreset(months);
    setForm((prev) => ({
      ...prev,
      startDate: subtractMonths(months),
      endDate: toDateString(new Date()),
    }));
  };

  const handleCustom = () => {
    setActivePreset('custom');
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!isFormValid) return;
    setQuery({ ...form });
  };

  const renderBody = () => {
    if (!query) {
      return (
        <tr>
          <td colSpan={COLUMNS.length} className="text-center py-4 text-muted">
            계좌번호와 조회 기간을 입력 후 조회하세요.
          </td>
        </tr>
      );
    }
    if (realizedPnlLoading) {
      return (
        <tr>
          <td colSpan={COLUMNS.length} className="text-center py-4 text-muted">
            불러오는 중...
          </td>
        </tr>
      );
    }
    if (realizedPnlError) {
      return (
        <tr>
          <td colSpan={COLUMNS.length} className="text-center py-4 text-danger">
            데이터를 불러오지 못했습니다.
          </td>
        </tr>
      );
    }
    if (realizedPnl.length === 0) {
      return (
        <tr>
          <td colSpan={COLUMNS.length} className="text-center py-4 text-muted">
            조회된 데이터가 없습니다.
          </td>
        </tr>
      );
    }
    return realizedPnl.map((row, i) => (
      <tr key={i}>
        {COLUMNS.map((col) => {
          const value = col.getValue ? col.getValue(row) : row[col.key];
          const extraClass = col.colorize ? pnlClass(value) : '';
          return (
            <td key={col.key} className={`text-${col.align} ${extraClass}`.trim()}>
              {col.render(value)}
            </td>
          );
        })}
      </tr>
    ));
  };

  return (
    <>
      <div className="card">
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div className="row g-3 align-items-end">
              <div className="col-auto">
                <label className="form-label">계좌</label>
                <select
                  name="accountId"
                  className="form-select"
                  value={form.accountId}
                  onChange={handleChange}
                  disabled={accountsLoading}
                >
                  {/* <option value="" disabled>계좌를 선택하세요</option> */}
                  {/* <option value="ALL">전체</option> */}
                  {accounts.map((account) => (
                    <option key={account.id} value={account.id}>
                      {account.alias} ({account.accountNumber})
                    </option>
                  ))}
                </select>
              </div>
              <div className="col-auto d-flex gap-1 align-self-end">
                {PERIOD_PRESETS.map(({ label, months }) => (
                  <button
                    key={label}
                    type="button"
                    className={`btn btn-sm ${activePreset === months ? 'btn-secondary' : 'btn-outline-secondary'}`}
                    onClick={() => handlePreset(months)}
                  >
                    {label}
                  </button>
                ))}
                <button
                  type="button"
                  className={`btn btn-sm ${isCustom ? 'btn-secondary' : 'btn-outline-secondary'}`}
                  onClick={handleCustom}
                >
                  직접선택
                </button>
              </div>
              <div className="col-auto">
                <label className="form-label">시작일자</label>
                <input
                  type="date"
                  name="startDate"
                  className="form-control"
                  value={form.startDate}
                  onChange={handleChange}
                  disabled={!isCustom}
                />
              </div>
              <div className="col-auto">
                <label className="form-label">종료일자</label>
                <input
                  type="date"
                  name="endDate"
                  className="form-control"
                  value={form.endDate}
                  onChange={handleChange}
                  disabled={!isCustom}
                />
              </div>
              <div className="col-auto">
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={!isFormValid}
                >
                  조회
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>

      <div className="card">
        <div className="card-header d-flex justify-content-between align-items-center">
          <h5 className="mb-0">실현손익</h5>
          {realizedPnl.length > 0 && (() => {
            const total = realizedPnl.reduce((sum, row) => sum + (row.realizedPnl ?? 0), 0);
            return (
              <span className={pnlClass(total)}>
                합계&nbsp;<strong>{formatKRW(total)}</strong>
              </span>
            );
          })()}
        </div>
        <div className="card-body p-0">
          <div className="table-responsive">
            <table className="table table-hover mb-0">
              <thead>
                <tr>
                  {COLUMNS.map((col) => (
                    <th key={col.key} className={`text-${col.align}`}>
                      {col.label}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>{renderBody()}</tbody>
            </table>
          </div>
        </div>
      </div>
      <div className="pb-4" />
    </>
  );
}
