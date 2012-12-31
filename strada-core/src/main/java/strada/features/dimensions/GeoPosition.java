package strada.features.dimensions;

import strada.features.BasicFeature;
import strada.features.Feature;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class GeoPosition extends BasicFeature
{

   /*
    * // XXX haystack index In the context of geospatial queries, haystack
    * indexes enhance searches by creating “bucket” of objects grouped by a
    * second criterion. For example, you might want all geographical searches to
    * also include the type of location being searched for. In this case, you
    * can create a haystack index that includes a document’s position and type:
    * 
    * db.places.ensureIndex( { position: "geoHaystack", type: 1 } ) You can then
    * query on position and type:
    * 
    * db.places.find( { position: [34.2, 33.3], type: "restaurant" } )
    */

   protected static Double[] fromObjArray(Object... params)
   {
      return params.length > 1 ? new Double[] { (Double) params[0], (Double) params[1] } : (Double[]) params[0];
   }

   private Double[] coordinates;

   public GeoPosition(String name, double lat, double lon)
   {
      this(name, new Double[] { lat, lon });
   }

   public GeoPosition(String name, Double[] coordinates)
   {
      super(name);

      Preconditions.checkNotNull(coordinates, "coordinates are required");
      this.coordinates = coordinates;
   }

   @Override
   public UpdateOp getUpdateOp()
   {
      return UpdateOp.SET;
   }

   @Override
   public Object getValue()
   {
      return coordinates;
   }

   @Override
   public String toString()
   {
      return Objects.toStringHelper(this.getClass()).add("coordinates", coordinates).toString();
   }

   @Override
   protected Feature createChildByName(String name, Object... params)
   {
      Preconditions.checkNotNull(params, "params are required");

      Double[] coords = fromObjArray(params);
      return new GeoPosition(name, coords);
   }

}
