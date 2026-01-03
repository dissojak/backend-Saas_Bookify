/**
 * Toast Notification System
 * Displays success, error, info, and warning messages
 */

console.log('toast.js loaded');

// Create toast container if it doesn't exist
function initToastContainer() {
    if (!document.getElementById('toastContainer')) {
        const container = document.createElement('div');
        container.id = 'toastContainer';
        container.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 10000;
            display: flex;
            flex-direction: column;
            gap: 10px;
            max-width: 400px;
        `;
        document.body.appendChild(container);
        console.log('Toast container created');
    }
}

function showToast(message, type = 'info', duration = 4000) {
    console.log('showToast called:', { message, type, duration });

    initToastContainer();

    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');

    // Determine colors based on type
    let bgColor, borderColor, textColor, icon;

    switch (type) {
        case 'success':
            bgColor = '#10b981';
            borderColor = '#059669';
            textColor = '#fff';
            icon = '✓';
            break;
        case 'error':
            bgColor = '#ef4444';
            borderColor = '#dc2626';
            textColor = '#fff';
            icon = '✕';
            break;
        case 'warning':
            bgColor = '#f59e0b';
            borderColor = '#d97706';
            textColor = '#fff';
            icon = '⚠';
            break;
        case 'info':
        default:
            bgColor = '#3b82f6';
            borderColor = '#2563eb';
            textColor = '#fff';
            icon = 'ℹ';
            break;
    }

    toast.style.cssText = `
        background: ${bgColor};
        border: 1px solid ${borderColor};
        color: ${textColor};
        padding: 16px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
        font-size: 14px;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 12px;
        animation: slideIn 0.3s ease;
        backdrop-filter: blur(10px);
    `;

    toast.innerHTML = `
        <span style="font-size: 18px; font-weight: bold;">${icon}</span>
        <span>${message}</span>
    `;

    container.appendChild(toast);
    console.log('Toast added to container');

    // Auto remove after duration
    if (duration > 0) {
        setTimeout(() => {
            toast.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    }

    // Allow manual close with click
    toast.style.cursor = 'pointer';
    toast.addEventListener('click', () => {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    });

    return toast;
}

// Add animations to document
function addToastStyles() {
    if (!document.getElementById('toastStyles')) {
        const style = document.createElement('style');
        style.id = 'toastStyles';
        style.textContent = `
            @keyframes slideIn {
                from {
                    transform: translateX(400px);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }

            @keyframes slideOut {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(400px);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
        console.log('Toast styles added');
    }
}

// Initialize on load
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', addToastStyles);
} else {
    addToastStyles();
}

// Export for use in other scripts
window.showToast = showToast;
console.log('window.showToast is now available');

