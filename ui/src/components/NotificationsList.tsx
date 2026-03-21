import React, { useState } from 'react';
import {
  BellIcon,
  CheckIcon,
  AtSignIcon,
  UserIcon,
  ArrowRightIcon,
  MessageSquareIcon,
  MailIcon } from
'lucide-react';
import { notifications, users } from '../data/mockData';
import { Avatar } from './ui/Avatar';
export function NotificationsList() {
  const [filter, setFilter] = useState<'all' | 'unread'>('all');
  const filteredNotifications = notifications.filter(
    (n) => filter === 'all' || !n.read
  );
  const getIconForType = (type: string) => {
    switch (type) {
      case 'mention':
        return <AtSignIcon className="w-4 h-4 text-purple-600" />;
      case 'assignment':
        return <UserIcon className="w-4 h-4 text-blue-600" />;
      case 'status_change':
        return <ArrowRightIcon className="w-4 h-4 text-green-600" />;
      case 'comment':
        return <MessageSquareIcon className="w-4 h-4 text-orange-600" />;
      case 'email':
        return <MailIcon className="w-4 h-4 text-gray-600" />;
      default:
        return <BellIcon className="w-4 h-4 text-gray-600" />;
    }
  };
  const getBgForType = (type: string) => {
    switch (type) {
      case 'mention':
        return 'bg-purple-100';
      case 'assignment':
        return 'bg-blue-100';
      case 'status_change':
        return 'bg-green-100';
      case 'comment':
        return 'bg-orange-100';
      case 'email':
        return 'bg-gray-100';
      default:
        return 'bg-gray-100';
    }
  };
  return (
    <div className="flex-1 flex flex-col h-screen bg-[#f8f9fa] overflow-hidden">
      {/* Header */}
      <div className="bg-white px-6 py-4 border-b border-gray-200 flex items-center justify-between shrink-0">
        <div className="flex items-center gap-2 text-lg">
          <span className="font-semibold text-gray-900">Notifications</span>
        </div>
      </div>

      {/* Toolbar */}
      <div className="bg-white px-6 py-3 border-b border-gray-200 flex items-center justify-between shrink-0">
        <div className="flex items-center gap-4">
          <button
            onClick={() => setFilter('all')}
            className={`text-sm font-medium pb-1 border-b-2 transition-colors ${filter === 'all' ? 'border-black text-black' : 'border-transparent text-gray-500 hover:text-gray-800'}`}>
            
            All
          </button>
          <button
            onClick={() => setFilter('unread')}
            className={`text-sm font-medium pb-1 border-b-2 transition-colors ${filter === 'unread' ? 'border-black text-black' : 'border-transparent text-gray-500 hover:text-gray-800'}`}>
            
            Unread
          </button>
        </div>
        <button className="flex items-center gap-2 px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 rounded-md transition-colors border border-gray-200">
          <CheckIcon className="w-4 h-4" /> Mark all as read
        </button>
      </div>

      {/* Feed Area */}
      <div className="flex-1 overflow-auto bg-white">
        <div className="max-w-3xl mx-auto py-6 px-4">
          <div className="space-y-4">
            {filteredNotifications.map((notification) => {
              const user = users.find((u) => u.id === notification.fromUserId);
              return (
                <div
                  key={notification.id}
                  className={`flex items-start gap-4 p-4 rounded-xl border transition-colors ${notification.read ? 'border-gray-100 bg-white' : 'border-blue-100 bg-blue-50/30'}`}>
                  
                  {/* Unread Indicator */}
                  <div className="w-2 h-2 mt-4 shrink-0">
                    {!notification.read &&
                    <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                    }
                  </div>

                  {/* Icon */}
                  <div
                    className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 ${getBgForType(notification.type)}`}>
                    
                    {getIconForType(notification.type)}
                  </div>

                  {/* Content */}
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      {user &&
                      <Avatar
                        src={user.avatar}
                        fallback={user.name}
                        size="sm" />

                      }
                      <span className="text-sm text-gray-900">
                        <span className="font-semibold">{user?.name}</span>{' '}
                        <span className="text-gray-600">
                          {notification.message}
                        </span>{' '}
                        <span className="font-medium">
                          {notification.relatedTo}
                        </span>
                      </span>
                    </div>
                    <div className="text-xs text-gray-500">
                      {notification.timeAgo}
                    </div>
                  </div>
                </div>);

            })}
            {filteredNotifications.length === 0 &&
            <div className="text-center py-12 text-gray-500">
                No notifications found.
              </div>
            }
          </div>
        </div>
      </div>
    </div>);

}