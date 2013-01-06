
var StatsTracker = {
	
	post : function(eventName, eventData) {
		this.session.time = this.UTC().getTime();
		this.changePendingEvents(1);
		this.image(this.eventUrl(this.session, eventName, eventData));
	},
	UTC : function() {
		var now = new Date();
		return new Date(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate(),  now.getUTCHours(), now.getUTCMinutes(), now.getUTCSeconds());
	},
	eventUrl : function(session, eventName, eventData) {
		var fragments = [ this.postUrl + '?__=' + eventName ];
		var escapeFn = window.encodeURIComponent || window.escape;
		// Collect session data.
		for ( var property in session) {
			if (typeof property === 'string'
					&& property.substring(0, 2) !== '__') {
				fragments.push('__' + property + '='
						+ escapeFn(session[property]));
			}
		}
		// Collect event data.
		for ( var eventProperty in eventData) {
			if (typeof eventProperty === 'string'
					&& eventProperty.substring(0, 2) !== '__') {
				fragments.push(eventProperty + '='
						+ escapeFn(eventData[eventProperty]));
			}
		}
		return fragments.join('&');
	},
	changePendingEvents : function(delta) {
		this.pendingEvents += delta;
	},
	image : function(url) {
		var img = new Image();
		img.src = url;
		img.onload = this.onImageLoad;
		var errorHandler = function() {
			setTimeout(function() {
				StatsTracker.image(url);
			}, 60000);
		};
		img.onerror = errorHandler;
		img.onabort = errorHandler;
	},
	onImageLoad : function(event) {
		StatsTracker.changePendingEvents(-1);
	},

	pendingEvents : 0,
	postUrl : '/stats/track',
	session : {}

};
