$(function() {

	var StatsApp = function() {
		this.init();
	}

	StatsApp.prototype = {
		init : function() {
			var that = this;
			this.datepicker(that);
		},
		resetpicker : function() {
			if ($('#datepicker-calendar').is(":visible")) {
				$('#datepicker-calendar').hide();
				$('#date-range-field a').html('&#9660;');
				$('#date-range-field').css({
					borderBottomLeftRadius : 5,
					borderBottomRightRadius : 5
				});
				$('#date-range-field a').css({
					borderBottomRightRadius : 5
				});
			}
		},
		datepicker : function(that) {
			var to = new Date();
			var from = new Date(to.getTime() - 1000 * 60 * 60 * 24 * 14);

			$('#datepicker-calendar').DatePicker(
					{
						inline : true,
						date : [ from, to ],
						calendars : 3,
						mode : 'range',
						current : new Date(to.getFullYear(), to.getMonth() - 1,
								1),
						onChange : function(dates, el) {
							// update the range display
							$('#date-range-field span').text(
									dates[0].getDate() + ' '
											+ dates[0].getMonthName(true)
											+ ', ' + dates[0].getFullYear()
											+ ' - ' + dates[1].getDate() + ' '
											+ dates[1].getMonthName(true)
											+ ', ' + dates[1].getFullYear());

							$('#date-value').val(
									dates[0].getDate() + '-'
											+ (dates[0].getMonth() + 1) + '-'
											+ dates[0].getFullYear() + '/'
											+ dates[1].getDate() + '-'
											+ (dates[1].getMonth() + 1) + '-'
											+ dates[1].getFullYear());
						}
					});

			$('#set-date').click(function(e) {
				e.stopPropagation();
				var base = '/stats/daily/';
				var path = window.location.pathname;
				var parts = path.split("/");
				if (parts.length > 3) {
					base = '/' + parts[1] + '/' + parts[2] + '/';
				}
				var dateVal = $('#date-value').val();
				if (dateVal == '/') {
					that.resetpicker();
					return;
				}
				window.location.href = base + dateVal;
			});

			// initialize the special date dropdown field
			$('#date-range-field span').text(
					from.getDate() + ' ' + from.getMonthName(true) + ', '
							+ from.getFullYear() + ' - ' + to.getDate() + ' '
							+ to.getMonthName(true) + ', ' + to.getFullYear());

			// bind a click handler to the date display field, which when
			// clicked
			// toggles the date picker calendar, flips the up/down indicator
			// arrow,
			// and keeps the borders looking pretty
			$('#date-range-field').bind('click', function() {
				$('#datepicker-calendar').toggle();
				if ($('#date-range-field a').text().charCodeAt(0) == 9660) {
					// switch to up-arrow
					$('#date-range-field a').html('&#9650;');
					$('#date-range-field').css({
						borderBottomLeftRadius : 0,
						borderBottomRightRadius : 0
					});
					$('#date-range-field a').css({
						borderBottomRightRadius : 0
					});
				} else {
					// switch to down-arrow
					$('#date-range-field a').html('&#9660;');
					$('#date-range-field').css({
						borderBottomLeftRadius : 5,
						borderBottomRightRadius : 5
					});
					$('#date-range-field a').css({
						borderBottomRightRadius : 5
					});
				}
				return false;
			});

			// global click handler to hide the widget calendar when it's open,
			// and
			// some other part of the document is clicked. Note that this works
			// best
			// defined out here rather than built in to the datepicker core
			// because this
			// particular example is actually an 'inline' datepicker which is
			// displayed
			// by an external event, unlike a non-inline datepicker which is
			// automatically
			// displayed/hidden by clicks within/without the datepicker element
			// and datepicker respectively
			$('html').click(function() {
				that.resetpicker();
			});

			// stop the click propagation when clicking on the calendar element
			// so that we don't close it
			$('#datepicker-calendar').click(function(event) {
				event.stopPropagation();
			});
		}
	};

	var App = new StatsApp();

});
