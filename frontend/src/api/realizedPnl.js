import { useMemo } from 'react';
import useSWR from 'swr';

const fetcher = (url) => fetch(url).then((res) => res.json());

export const endpoints = {
  key: '/api/assets/realized-pnl'
};

export function useGetRealizedPnl(params) {
  const { accountId, startDate, endDate } = params ?? {};

  const buildKey = () => {
    if (!startDate || !endDate || accountId === undefined) return null;
    const searchParams = new URLSearchParams({ startDate, endDate });
    if (accountId !== 'ALL') searchParams.set('accountId', accountId);
    return `${endpoints.key}?${searchParams.toString()}`;
  };

  const { data, isLoading, error } = useSWR(buildKey(), fetcher);

  const memoizedValue = useMemo(
    () => ({
      realizedPnl: Array.isArray(data?.data?.items) ? data.data.items : [],
      realizedPnlLoading: isLoading,
      realizedPnlError: error
    }),
    [data, isLoading, error]
  );

  return memoizedValue;
}
