import React, { useState } from 'react';
import { ReceiptTextIcon, ChevronDownIcon, BadgeDollarSignIcon } from 'lucide-react';
import { AuthSession, InvoiceRecord, QuoteRecord } from '../types/crm';
import { StatusBadge } from './ui/StatusBadge';
import { Modal } from './ui/Modal';
import { convertQuoteToInvoice, createQuote, updateQuoteStatus } from '../lib/api';

interface CommerceListProps {
  quotes?: QuoteRecord[];
  invoices?: InvoiceRecord[];
  session: AuthSession;
  onRefresh: () => Promise<void>;
}

function mapCommerceStatus(status: string) {
  switch (status) {
    case 'DRAFT':
      return 'New';
    case 'APPROVED':
    case 'ISSUED':
      return 'Qualified';
    case 'PAID':
    case 'CONVERTED':
      return 'Contacted';
    case 'CANCELLED':
    case 'REFUNDED':
      return 'Nurture';
    default:
      return 'New';
  }
}

function money(value: number) {
  return `$${value.toLocaleString()}`;
}

function dateText(value: string | null) {
  return value ? new Date(value).toLocaleDateString() : '-';
}

export function CommerceList({ quotes = [], invoices = [], session, onRefresh }: CommerceListProps) {
  const [selectedQuoteId, setSelectedQuoteId] = useState<number | null>(quotes[0]?.id ?? null);
  const [statusValue, setStatusValue] = useState('APPROVED');
  const [query, setQuery] = useState('');
  const [message, setMessage] = useState<string | null>(null);
  const [pending, setPending] = useState<'convert' | 'status' | null>(null);
  const [creating, setCreating] = useState(false);
  const [createForm, setCreateForm] = useState({ accountId: '', name: '', amount: '', status: 'DRAFT', validUntil: '' });
  const selectedQuote = quotes.find((quote) => quote.id === selectedQuoteId) ?? quotes[0] ?? null;
  const filteredQuotes = quotes.filter((quote) =>
    [quote.name, quote.accountName, quote.status, String(quote.amount)]
      .join(' ')
      .toLowerCase()
      .includes(query.toLowerCase()),
  );
  const filteredInvoices = invoices.filter((invoice) =>
    [invoice.invoiceNumber, invoice.accountName, invoice.status, String(invoice.amount)]
      .join(' ')
      .toLowerCase()
      .includes(query.toLowerCase()),
  );

  async function handleConvert() {
    if (!selectedQuote) {
      return;
    }
    setPending('convert');
    setMessage(null);
    try {
      await convertQuoteToInvoice(session, selectedQuote.id);
      setMessage('Quote converted to invoice.');
      await onRefresh();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Unable to convert quote.');
    } finally {
      setPending(null);
    }
  }

  async function handleStatusUpdate() {
    if (!selectedQuote) {
      return;
    }
    setPending('status');
    setMessage(null);
    try {
      await updateQuoteStatus(session, selectedQuote.id, {
        status: statusValue,
        note: 'Updated from UI',
      });
      setMessage('Quote status updated.');
      await onRefresh();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Unable to update quote.');
    } finally {
      setPending(null);
    }
  }

  return (
    <div className="flex min-h-0 flex-1 flex-col bg-[#f8f9fa] overflow-hidden">
      <div className="bg-white px-4 py-4 border-b border-gray-200 flex flex-col gap-3 sm:px-6 sm:flex-row sm:items-center sm:justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <span className="text-gray-500">Commerce /</span>
          <button className="flex items-center gap-2 font-semibold text-gray-900 hover:bg-gray-50 px-2 py-1 rounded-md transition-colors">
            <ReceiptTextIcon className="w-5 h-5 text-gray-400" />
            Overview
            <ChevronDownIcon className="w-4 h-4 text-gray-400" />
          </button>
        </div>
        <button onClick={() => setCreating(true)} className="rounded-md border border-gray-200 bg-gray-50 px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-white transition-colors">
          New quote
        </button>
      </div>

      <Modal isOpen={creating} onClose={() => setCreating(false)} title="Create Quote" width="sm">
        <div className="space-y-4">
          <div className="space-y-3">
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Account ID</span>
              <input value={createForm.accountId} onChange={(e) => setCreateForm((c) => ({ ...c, accountId: e.target.value }))} type="number" placeholder="e.g. 1" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Quote Name</span>
              <input value={createForm.name} onChange={(e) => setCreateForm((c) => ({ ...c, name: e.target.value }))} placeholder="e.g. Q4 Marketing Contract" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
            <div className="grid grid-cols-2 gap-3">
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Amount ($)</span>
                <input value={createForm.amount} onChange={(e) => setCreateForm((c) => ({ ...c, amount: e.target.value }))} type="number" placeholder="5000" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
              </label>
              <label className="block">
                <span className="mb-1 block text-xs font-medium text-gray-700">Status</span>
                <select value={createForm.status} onChange={(e) => setCreateForm((c) => ({ ...c, status: e.target.value }))} className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white"><option value="DRAFT">Draft</option><option value="APPROVED">Approved</option></select>
              </label>
            </div>
            <label className="block">
              <span className="mb-1 block text-xs font-medium text-gray-700">Valid Until</span>
              <input value={createForm.validUntil} onChange={(e) => setCreateForm((c) => ({ ...c, validUntil: e.target.value }))} type="date" className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-900 outline-none transition focus:border-gray-400 focus:bg-white" />
            </label>
          </div>
          <div className="mt-6 flex justify-end gap-3 border-t border-gray-100 pt-4">
            <button onClick={() => setCreating(false)} className="rounded-lg px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-50 transition-colors">
              Cancel
            </button>
            <button
              onClick={async () => {
                setPending('status');
                setMessage(null);
                try {
                  await createQuote(session, {
                    accountId: Number(createForm.accountId),
                    name: createForm.name,
                    amount: Number(createForm.amount),
                    status: createForm.status,
                    validUntil: createForm.validUntil || null,
                  });
                  setCreateForm({ accountId: '', name: '', amount: '', status: 'DRAFT', validUntil: '' });
                  setCreating(false);
                  setMessage('Quote created successfully.');
                  await onRefresh();
                } catch (error) {
                  setMessage(error instanceof Error ? error.message : 'Unable to create quote.');
                } finally {
                  setPending(null);
                }
              }}
              disabled={pending !== null || !createForm.accountId || !createForm.name || !createForm.amount}
              className="rounded-lg bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
            >
              {pending === 'status' ? 'Saving...' : 'Save Quote'}
            </button>
          </div>
        </div>
      </Modal>

      <div className="border-b border-gray-200 bg-white px-4 py-3 sm:px-6">
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search quotes and invoices"
          className="w-full max-w-md rounded-md border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-700"
        />
      </div>

      <div className="min-h-0 flex-1 overflow-auto p-4 sm:p-6 space-y-6">
        <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="text-sm text-gray-500">Open Quotes</div>
            <div className="mt-2 text-2xl font-semibold text-gray-900">{filteredQuotes.length}</div>
          </div>
          <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="text-sm text-gray-500">Invoices</div>
            <div className="mt-2 text-2xl font-semibold text-gray-900">{filteredInvoices.length}</div>
          </div>
          <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="text-sm text-gray-500">Tracked Value</div>
            <div className="mt-2 text-2xl font-semibold text-gray-900">
              {money(
                filteredQuotes.reduce((sum, quote) => sum + quote.amount, 0) +
                  filteredInvoices.reduce((sum, invoice) => sum + invoice.amount, 0),
              )}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 gap-6 xl:grid-cols-[1.2fr_0.8fr]">
          <div className="space-y-6">
          <div className="rounded-xl border border-gray-200 bg-white shadow-sm">
            <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
              <div className="font-semibold text-gray-900">Quotes</div>
              <BadgeDollarSignIcon className="h-4 w-4 text-gray-400" />
            </div>
            <div className="overflow-x-auto">
              <table className="w-full whitespace-nowrap text-left text-sm">
                <thead className="bg-gray-50 text-xs text-gray-500">
                  <tr>
                    <th className="px-5 py-3 font-medium">Quote</th>
                    <th className="px-5 py-3 font-medium">Account</th>
                    <th className="px-5 py-3 font-medium">Amount</th>
                    <th className="px-5 py-3 font-medium">Status</th>
                    <th className="px-5 py-3 font-medium">Valid Until</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {filteredQuotes.map((quote) => (
                    <tr key={quote.id} className={`hover:bg-gray-50 cursor-pointer ${selectedQuote?.id === quote.id ? 'bg-gray-50' : ''}`} onClick={() => setSelectedQuoteId(quote.id)}>
                      <td className="px-5 py-3 font-medium text-gray-900">{quote.name}</td>
                      <td className="px-5 py-3 text-gray-600">{quote.accountName}</td>
                      <td className="px-5 py-3 text-gray-900">{money(quote.amount)}</td>
                      <td className="px-5 py-3"><StatusBadge status={mapCommerceStatus(quote.status)} /></td>
                      <td className="px-5 py-3 text-gray-500">{dateText(quote.validUntil)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {!filteredQuotes.length ? <div className="px-5 py-10 text-center text-sm text-gray-500">No quotes match the current search yet.</div> : null}
            </div>
          </div>

          <div className="rounded-xl border border-gray-200 bg-white shadow-sm">
            <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
              <div className="font-semibold text-gray-900">Invoices</div>
              <BadgeDollarSignIcon className="h-4 w-4 text-gray-400" />
            </div>
            <div className="overflow-x-auto">
              <table className="w-full whitespace-nowrap text-left text-sm">
                <thead className="bg-gray-50 text-xs text-gray-500">
                  <tr>
                    <th className="px-5 py-3 font-medium">Invoice</th>
                    <th className="px-5 py-3 font-medium">Account</th>
                    <th className="px-5 py-3 font-medium">Amount</th>
                    <th className="px-5 py-3 font-medium">Status</th>
                    <th className="px-5 py-3 font-medium">Due At</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {filteredInvoices.map((invoice) => (
                    <tr key={invoice.id} className="hover:bg-gray-50">
                      <td className="px-5 py-3 font-medium text-gray-900">{invoice.invoiceNumber}</td>
                      <td className="px-5 py-3 text-gray-600">{invoice.accountName}</td>
                      <td className="px-5 py-3 text-gray-900">{money(invoice.amount)}</td>
                      <td className="px-5 py-3"><StatusBadge status={mapCommerceStatus(invoice.status)} /></td>
                      <td className="px-5 py-3 text-gray-500">{dateText(invoice.dueAt)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {!filteredInvoices.length ? <div className="px-5 py-10 text-center text-sm text-gray-500">No invoices match the current search yet.</div> : null}
            </div>
          </div>
          </div>

          <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="mb-4 font-semibold text-gray-900">Quote actions</div>
            {selectedQuote ? (
              <div className="space-y-5">
                <div>
                  <div className="text-sm font-medium text-gray-900">{selectedQuote.name}</div>
                  <div className="mt-1 text-sm text-gray-500">{selectedQuote.accountName} · {money(selectedQuote.amount)}</div>
                </div>

                <div className="space-y-2">
                  <label className="text-sm font-medium text-gray-700">Quote status</label>
                  <select
                    value={statusValue}
                    onChange={(event) => setStatusValue(event.target.value)}
                    className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm text-gray-700"
                  >
                    <option value="DRAFT">Draft</option>
                    <option value="APPROVED">Approved</option>
                    <option value="CANCELLED">Cancelled</option>
                  </select>
                  <button
                    onClick={handleStatusUpdate}
                    className="w-full rounded-md border border-gray-200 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:bg-gray-100"
                    disabled={pending !== null}
                  >
                    {pending === 'status' ? 'Updating...' : 'Update Quote'}
                  </button>
                </div>

                <button
                  onClick={handleConvert}
                  className="w-full rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-800 disabled:bg-gray-400"
                  disabled={pending !== null}
                >
                  {pending === 'convert' ? 'Converting...' : 'Convert To Invoice'}
                </button>

                {message ? <div className="rounded-lg border border-gray-200 bg-gray-50 px-3 py-3 text-sm text-gray-700">{message}</div> : null}
              </div>
            ) : (
              <div className="text-sm text-gray-500">Select a quote to run commerce actions.</div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
