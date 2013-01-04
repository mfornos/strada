var Funnel = function(id, data) {
	this.init(id, data);
}

Funnel.prototype = {
	init : function(id, data) {
		var that = this;
		this.show(that, id, data);
	},
	div : function(css) {
		return $(document.createElement('div')).addClass(css);
	},
	show : function(t, id, data) {

		var steps = data.levels.length;
		var container = t.div('fluid counters');

		$.each(data.levelsPrint, function(index, level) {
			var wrapper = t.div('std');
			var legend = t.div('legend');
			var bar = t.div('bar');
			var fill = t.div('fill');
			fill.width(data.conversionRates[index] + '%');

			container.append(wrapper);
			wrapper.append(legend);
			bar.append(fill);
			legend.append(t.div('step').text(data.labels[index]));
			legend.append(t.div('step-value').text(level));
			wrapper.append(legend);
			legend.append(bar);
			$(id).append(container);

			if (index < steps - 1) {
				var ratio = t.div('ratio');
				ratio.text('⇣ ' + data.conversionRates[index + 1] + '%');
				legend.append(ratio);
			}
		});

	}
};
