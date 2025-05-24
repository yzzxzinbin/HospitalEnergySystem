/**
 * Parse the time to string
 * @param {(Object|string|number)} time
 * @param {string} cFormat
 * @returns {string | null}
 */
export function parseTime(time, cFormat) {
  if (arguments.length === 0 || !time) {
    return null;
  }
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}';
  let date;
  if (typeof time === 'object') {
    date = time; // Already a Date object
  } else {
    let timeInput = time; // Keep original for potential re-parse
    if (typeof timeInput === 'string') {
      if (/^[0-9]+$/.test(timeInput)) {
        // Support "1548221490638" (numeric string timestamp)
        timeInput = parseInt(timeInput);
      } else {
        // For ISO 8601 datetime strings (e.g., "2023-10-26T10:30:00"),
        // direct parsing is preferred and usually works in modern browsers.
        // The replace(/-/gm, '/') is mainly for "YYYY-MM-DD" date-only strings in Safari.
        if (!timeInput.includes('T')) { // If it's likely a date-only string
          timeInput = timeInput.replace(/-/gm, '/');
        }
        // Otherwise, for full datetime strings with 'T', pass as is.
      }
    }

    if (typeof timeInput === 'number' && timeInput.toString().length === 10) {
      // Support Unix timestamp (seconds)
      timeInput = timeInput * 1000;
    }
    
    date = new Date(timeInput);

    // Fallback: If parsing failed and original input was a string that was NOT modified,
    // OR if it was modified but still failed, try original string if it was an ISO string.
    // This handles cases where the initial logic might not have perfectly prepared the string.
    if (isNaN(date.getTime()) && typeof time === 'string') {
        if (time.includes('T') && time.includes('-') && time !== timeInput) {
            // If original was ISO and got modified (e.g. by replace for non-T strings) and failed, try original ISO
            date = new Date(time);
        } else if (time.includes('T') && time.includes('-') && time === timeInput) {
            // If original was ISO, was not modified, but still failed (very unlikely for valid ISO)
            // This path is mostly a safeguard; direct new Date(validISO) should work.
        } else if (time !== timeInput) { // If it was modified (e.g. YYYY-MM-DD to YYYY/MM/DD) and failed
             date = new Date(time); // Try original string again
        }
    }
  }

  // If date is still invalid after all attempts, return null or an error string.
  // The original function would proceed and produce "0-0-0 0:0:0".
  // To maintain consistency with that behavior if date is invalid:
  if (isNaN(date.getTime())) {
    // console.warn('parseTime: Could not parse input into a valid date:', time);
    // To produce "0-0-0..." like the original when date is invalid:
    const invalidFormatObj = { y: 0, m: 0, d: 0, h: 0, i: 0, s: 0, a: 0 };
    return format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
      if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][0]; } // Default to Sunday
      let value = invalidFormatObj[key];
      if (result.length > 0 && value < 10) {
        value = '0' + value;
      }
      return value;
    });
  }

  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  };
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key];
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value]; }
    if (result.length > 0 && value < 10) {
      value = '0' + value;
    }
    return value || 0;
  });
  return time_str;
}

