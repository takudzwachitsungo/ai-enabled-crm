import React from 'react';
import { ReceiptTextIcon, ChevronDownIcon, BadgeDollarSignIcon } from 'lucide-react';
import { InvoiceRecord, QuoteRecord } from '../types/crm';
import { StatusBadge } from './ui/StatusBadge';

interface CommerceListProps {
  quotes?: QuoteRecord[];
  invoices?: InvoiceRecord[];
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

export function CommerceList({ quotes = [], invoices = [] }: CommerceListProps) {
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
        <div className="rounded-md border border-gray-200 bg-gray-50 px-3 py-1.5 text-sm font-medium text-gray-500">
          Quotes and invoices
        </div>
      </div>

      <div className="min-h-0 flex-1 overflow-auto p-4 sm:p-6 space-y-6">
        <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
          <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="text-sm text-gray-500">Open Quotes</div>
            <div className="mt-2 text-2xl font-semibold text-gray-900">{quotes.length}</div>
          </div>
          <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="text-sm text-gray-500">Invoices</div>
            <div className="mt-2 text-2xl font-semibold text-gray-900">{invoices.length}</div>
          </div>
          <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
            <div className="text-sm text-gray-500">Tracked Value</div>
            <div className="mt-2 text-2xl font-semibold text-gray-900">
              {money(
                quotes.reduce((sum, quote) => sum + quote.amount, 0) +
                  invoices.reduce((sum, invoice) => sum + invoice.amount, 0),
              )}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 gap-6 xl:grid-cols-2">
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
                  {quotes.map((quote) => (
                    <tr key={quote.id} className="hover:bg-gray-50">
                      <td className="px-5 py-3 font-medium text-gray-900">{quote.name}</td>
                      <td className="px-5 py-3 text-gray-600">{quote.accountName}</td>
                      <td className="px-5 py-3 text-gray-900">{money(quote.amount)}</td>
                      <td className="px-5 py-3"><StatusBadge status={mapCommerceStatus(quote.status)} /></td>
                      <td className="px-5 py-3 text-gray-500">{dateText(quote.validUntil)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
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
                  {invoices.map((invoice) => (
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
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
