import { useMemo } from 'react';
import useSWR from 'swr';

const fetcher = (url) => fetch(url).then((res) => res.json());

export const endpoints = {
  key: '/api/accounts'
};

export function useGetAccounts() {
  const { data, isLoading, error } = useSWR(endpoints.key, fetcher);

  const memoizedValue = useMemo(
    () => ({
      accounts: Array.isArray(data?.data?.accounts) ? data.data.accounts : [],
      accountsLoading: isLoading,
      accountsError: error
    }),
    [data, isLoading, error]
  );

  return memoizedValue;
}
