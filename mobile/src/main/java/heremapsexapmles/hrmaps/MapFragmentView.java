package heremapsexapmles.hrmaps;

/**
 * Created by omand067 on 06/01/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.RoadElement;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Maneuver;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.here.sdk.analytics.internal.LocationRequest;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

        /* This class encapsulates the properties and functionality of the Map view.It also triggers a
        * turn-by-turn navigation from HERE Burnaby office to Langley BC.There is a sample voice skin
        * bundled within the SDK package to be used out-of-box, please refer to the Developer's guide for
        * the usage.
        */

public class MapFragmentView {
    private MapFragment m_mapFragment;
    private Activity m_activity;
    private Button m_naviControlButton;
    private Map m_map;
    private NavigationManager m_navigationManager;
    private GeoBoundingBox m_geoBoundingBox;
    private Route m_route;
    private Button m_GetLocationButton;
    private MapMarker m_positionIndicatorFixed = null;
    private boolean m_returningToRoadViewMode = false;
    //private MapTrafficLayer traffic = null;
    private ImageButton m_settingsBtn;
    private SettingsPanel m_settingsPanel;
    private LinearLayout m_settingsLayout;
    private Button m_Button;


    private List<MapObject> m_mapObjectList = new ArrayList<>();
    // HERE location data source instance
    private LocationDataSourceHERE mHereLocation;

    // flag that indicates whether maps is being transformed
    private boolean mTransforming;

    // callback that is called when transforming ends
    private Runnable mPendingUpdate;

    // text view instance for showing location information
    private TextView mLocationInfo;

    private LocationRequest mLocationRequest;

    // positioning manager instance
    private PositioningManager mPositioningManager;

    // Set this to PositioningManager.getInstance() upon Engine Initialization
    // private PositioningManager posManager;

    public MapFragmentView(Activity activity)  {
        m_activity = activity;
        initMapFragment();
        initGetLocationButton();
        initNaviControlButton();
        initSettingsPanel();
        m_Button = (Button) m_activity.findViewById(R.id.detailbutton);
        m_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(m_activity, "Button Pressed", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent( m_activity ,   TripDetails.class ) ;
             m_activity.startActivity(intent);
            }
        });
    }

    Context context;

    private void initMapFragment() {
        /* Locate the mapFragment UI element */
        m_mapFragment = (MapFragment) m_activity.getFragmentManager()
                .findFragmentById(R.id.mapfragment);

        if (m_mapFragment != null) {
            /* Initialize the MapFragment, results will be given via the called back. */
            m_mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                    if (error == Error.NONE) {
                        m_map = m_mapFragment.getMap();
                        System.out.println("mapFragment.getMap()= "+m_mapFragment.getMap());


                        //m_map.setCenter(new GeoCoordinate(45.425533, -75.692482),Map.Animation.LINEAR);
                        PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                        NavigationManager.getInstance().setMap(m_map);
                        mPositioningManager = PositioningManager.getInstance();
                        m_map.setCenter(PositioningManager.getInstance().getPosition().getCoordinate(),Map.Animation.BOW);
                        System.out.println("map.setCentre= "+m_map.getCenter());
                        m_map.setZoomLevel(13.2);

                       /* // listen to real position updates. This is used when RoadView is
                        // not active.
                        PositioningManager.getInstance().addListener(
                                new WeakReference<PositioningManager.OnPositionChangedListener>(mapPositionHandler));
*/
                        /*
                         * Get the NavigationManager instance.It is responsible for providing voice
                         * and visual instructions while driving and walking
                         */
                        m_navigationManager = NavigationManager.getInstance();



                    } else {
                        Toast.makeText(m_activity,
                                "ERROR: Cannot initialize Map with error " + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }





   /* // Resume positioning listener on wake up
    public void onResume() {
        super.onResume();
        paused = false;
        if (posManager != null) {
            posManager.start(
                    PositioningManager.LocationMethod.GPS_NETWORK);
        }
    }
    // To pause positioning listener
    public void onPause() {
        if (posManager != null) {
            posManager.stop();
        }
        super.onPause();
        paused = true;
    }
    // To remove the positioning listener
    public void onDestroy() {
        if (posManager != null) {
// Cleanup
            posManager.removeListener(
                    positionListener);
        }
        m_map = null;
        super.onDestroy();
    }

    // Define positioning listener
    private PositioningManager.OnPositionChangedListener positionListener = new
            PositioningManager.OnPositionChangedListener() {
                public void onPositionUpdated(PositioningManager.LocationMethod method,
                                              GeoPosition position, boolean isMapMatched) {
// set the center only when the app is in the foreground
// to reduce CPU consumption
                    if (!paused) {
                        m_map.setCenter(position.getCoordinate(),
                                Map.Animation.NONE);}
                }
                public void onPositionFixChanged(PositioningManager.LocationMethod method,
                                                 PositioningManager.LocationStatus status) {
                }
            };
    // Register positioning listener
PositioningManager.getInstance().addListener( new WeakReference<OnPositionChangedListener>(positionListener));
*/

    // listen for positioning events
    private PositioningManager.OnPositionChangedListener mapPositionHandler = new PositioningManager.OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod method, GeoPosition position,
                                      boolean isMapMatched) {
            if (NavigationManager.getInstance().getMapUpdateMode().equals(NavigationManager
                    .MapUpdateMode.NONE) && !m_returningToRoadViewMode)
                // use this updated position when map is not updated by RoadView.
                m_positionIndicatorFixed.setCoordinate(position.getCoordinate());
        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod method,
                                         PositioningManager.LocationStatus status) {

        }
    };


    private void currentLocation(){

        PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);
        NavigationManager.getInstance().setMap(m_map);
        m_map.setCenter(PositioningManager.getInstance().getPosition().getCoordinate(),Map.Animation.BOW);

        Image icon = new Image();
        m_positionIndicatorFixed = new MapMarker();
        m_positionIndicatorFixed.setVisible(false);
        m_positionIndicatorFixed.setCoordinate(m_map.getCenter());
        m_map.addMapObject(m_positionIndicatorFixed);
        m_mapObjectList.add(m_positionIndicatorFixed);
        m_mapFragment.getPositionIndicator().setVisible(false);

        // create a map marker to show current position
        try {

            icon.setImageResource(R.drawable.gps_position);
            m_positionIndicatorFixed.setIcon(icon);
            m_map.setZoomLevel(16);
            m_positionIndicatorFixed.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void createRoute() {
        /* Initialize a CoreRouter */
        CoreRouter coreRouter = new CoreRouter();

        /* Initialize a RoutePlan */
        RoutePlan routePlan = new RoutePlan();

        /*
         * Initialize a RouteOption.HERE SDK allow users to define their own parameters for the
         * route calculation,including transport modes,route types and route restrictions etc.Please
         * refer to API doc for full list of APIs
         */
        RouteOptions routeOptions = new RouteOptions();
        /* Other transport modes are also available e.g Pedestrian */
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        /* Enable highway in this route. */
        routeOptions.setHighwaysAllowed(true);
        /* Calculate the fastest route available. */
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        //routeOptions.setRouteType(RouteOptions.Type.BALANCED);
        //routeOptions.setRouteType(RouteOptions.Type.SHORTEST);

        /* Calculate 1 route. */
        routeOptions.setRouteCount(1);
        /* Finally set the route option */
        routePlan.setRouteOptions(routeOptions);

        /* Define waypoints for the route */
        /* START: the user's location */
        RouteWaypoint startPoint = new RouteWaypoint(PositioningManager.getInstance().getPosition().
                getCoordinate());
        /* END: Walmart Trayinyards */
        RouteWaypoint destination = new RouteWaypoint(new GeoCoordinate(45.4137778, -75.6509677));

        /* Add both waypoints to the route plan */
        routePlan.addWaypoint(startPoint);
        routePlan.addWaypoint(destination);


        /* Trigger the route calculation,results will be called back via the listener */
        coreRouter.calculateRoute(routePlan,
                new Router.Listener<List<RouteResult>, RoutingError>() {

                    @Override
                    public void onProgress(int i) {
                        /* The calculation progress can be retrieved in this callback. */
                    }

                    @Override
                    public void onCalculateRouteFinished(List<RouteResult> routeResults,
                                                         RoutingError routingError) {
                        /* Calculation is done.Let's handle the result */
                        if (routingError == RoutingError.NONE) {
                            if (routeResults.get(0).getRoute() != null) {

                                m_route = routeResults.get(0).getRoute();
                                /* Create a MapRoute so that it can be placed on the map */
                                MapRoute mapRoute = new MapRoute(routeResults.get(0).getRoute());

                                /* Show the maneuver number on top of the route */
                                mapRoute.setManeuverNumberVisible(true);

                                /* Add the MapRoute to the map */
                                m_map.addMapObject(mapRoute);
                                m_mapObjectList.add(mapRoute);
                                /*
                                 * We may also want to make sure the map view is orientated properly
                                 * so the entire route can be easily seen.
                                 */
                                m_geoBoundingBox = routeResults.get(0).getRoute().getBoundingBox();
                                m_map.zoomTo(m_geoBoundingBox, Map.Animation.NONE,
                                        Map.MOVE_PRESERVE_ORIENTATION);

                                startNavigation(m_route);
                            } else {
                                Toast.makeText(m_activity,
                                        "Error:route results returned is not valid",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(m_activity,
                                    "Error:route calculation returned error code: " + routingError,
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }
    //test
    private void initGetLocationButton(){
        m_GetLocationButton =(Button) m_activity.findViewById(R.id.getLocationButton);
        m_GetLocationButton.setText(R.string.get_location);
        m_GetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_map != null && m_positionIndicatorFixed != null) {
                    m_map.removeMapObject(m_positionIndicatorFixed);
                    m_positionIndicatorFixed = null;
                    m_map.setCenter(PositioningManager.getInstance().getPosition().getCoordinate(),Map.Animation.BOW);


                } else {
                    m_map.setCenter(PositioningManager.getInstance().getPosition().getCoordinate(),Map.Animation.BOW);
                    currentLocation();
                } }
        });

    }
    private void initNaviControlButton() {
        m_naviControlButton = (Button) m_activity.findViewById(R.id.naviCtrlButton);
        m_naviControlButton.setText(R.string.start_navi);

        m_naviControlButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                /*
                 * To start a turn-by-turn navigation, a concrete route object is required.We use
                 * the same steps from Routing sample app to create a route from 4350 Still Creek Dr
                 * to Langley BC without going on HWY.
                 *
                 * The route calculation requires local map data.Unless there is pre-downloaded map
                 * data on device by utilizing MapLoader APIs,it's not recommended to trigger the
                 * route calculation immediately after the MapEngine is initialized.The
                 * INSUFFICIENT_MAP_DATA error code may be returned by CoreRouter in this case.
                 *
                 */
                if (m_route == null) {
                    createRoute();
                } else {
                    m_navigationManager.stop();
                    //cleanMap();
                    /*
                     * Restore the map orientation to show entire route on screen
                     */
                    m_map.zoomTo(m_geoBoundingBox, Map.Animation.NONE, 0f);
                    m_naviControlButton.setText(R.string.start_navi);
                    m_route = null;
                    // m_map.removeMapObject(m_route);
                    m_map.removeMapObjects((List<MapObject>) m_route);
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void startNavigation(Route route) {

       // DataClient mDataClient = Wearable.getDataClient(onCreate.startMeasure());

        m_naviControlButton.setText(R.string.stop_navi);
        /* Display the position indicator on map */
        m_map.getPositionIndicator().setVisible(true);
        /* Configure Navigation manager to launch navigation on current map */
        m_navigationManager.setMap(m_map);
        /*
         * Start the turn-by-turn navigation.Please note if the transport mode of the passed-in
         * route is pedestrian, the NavigationManager automatically triggers the guidance which is
         * suitable for walking. Simulation and tracking modes can also be launched at this moment
         * by calling either simulate() or startTracking()
         */
       // m_navigationManager.startNavigation(route);

 /* Choose navigation modes between real time navigation and simulation */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_activity);
        alertDialogBuilder.setTitle("Navigation");
        alertDialogBuilder.setMessage("Choose Mode");
        alertDialogBuilder.setNegativeButton("Navigation",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                m_navigationManager.startNavigation(m_route);
                m_map.setTilt(60);
            };
        });
        alertDialogBuilder.setPositiveButton("Simulation",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                m_navigationManager.simulate(m_route,60);//Simualtion speed is set to 60 m/s
                m_map.setTilt(60);
            };
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        /*
         * Set the map update mode to ROADVIEW.This will enable the automatic map movement based on
         * the current location.If user gestures are expected during the navigation, it's
         * recommended to set the map update mode to NONE first. Other supported update mode can be
         * found in HERE Android SDK API doc
         */
        m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);


        /*
         * NavigationManager contains a number of listeners which we can use to monitor the
         * navigation status and getting relevant instructions.In this example, we will add 2
         * listeners for demo purpose,please refer to HERE Android SDK API documentation for details
         */
        addNavigationListeners();

    }

    private void addNavigationListeners() {

        /*
         * Register a NavigationManagerEventListener to monitor the status change on
         * NavigationManager
         */
        m_navigationManager.addNavigationManagerEventListener(
                new WeakReference<NavigationManager.NavigationManagerEventListener>(
                        m_navigationManagerEventListener));

        /* Register a PositionListener to monitor the position updates */
        m_navigationManager.addPositionListener(
                new WeakReference<NavigationManager.PositionListener>(m_positionListener));


        m_navigationManager.addManeuverEventListener(new WeakReference<NavigationManager.ManeuverEventListener>(m_maneuverListener));
          //      new WeakReference<NavigationManager.PositionListener>(m_maneuverListener));


    }



    private NavigationManager.PositionListener m_positionListener = new NavigationManager.PositionListener() {
        @Override
        public void onPositionUpdated(GeoPosition geoPosition) {
            /* Current position information can be retrieved in this callback */
        }
    };

    private NavigationManager.NavigationManagerEventListener m_navigationManagerEventListener = new NavigationManager.NavigationManagerEventListener() {
        @Override
        public void onRunningStateChanged() {
            Toast.makeText(m_activity, "Running state changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNavigationModeChanged() {
            Toast.makeText(m_activity, "Navigation mode changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnded(NavigationManager.NavigationMode navigationMode) {
            Toast.makeText(m_activity, navigationMode + " was ended", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onMapUpdateModeChanged(NavigationManager.MapUpdateMode mapUpdateMode) {
            Toast.makeText(m_activity, "Map update mode is changed to " + mapUpdateMode,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRouteUpdated(Route route) {
            Toast.makeText(m_activity, "Route updated", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCountryInfo(String s, String s1) {
            Toast.makeText(m_activity, "Country info updated from " + s + " to " + s1,
                    Toast.LENGTH_SHORT).show();
        }


    };

    private NavigationManager.ManeuverEventListener m_maneuverListener = new NavigationManager.ManeuverEventListener (){
        @Override
        public void onManeuverEvent() {
            Toast.makeText(m_activity, "MANEUVERRRRR",Toast.LENGTH_SHORT).show();


            RoadElement roadElement  =  PositioningManager.getInstance().getRoadElement();




            HRDbAdapter hrDbAdapter= new HRDbAdapter(  m_activity );
            hrDbAdapter.open();
            DataHolder dataHolder= new DataHolder(roadElement.getRoadName(), roadElement.getNumberOfLanes()+"", DateFormat.getDateTimeInstance().format(new Date()) );
            long id=hrDbAdapter.InsertinDatabase(dataHolder);

            if(id!=-1){
                Toast.makeText(m_activity, "Inserted data => "+ id , Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(m_activity, "Unable to Insert data => "+ id , Toast.LENGTH_SHORT).show();
            }

            Log.d("Test ", "maneuver.....");
        }


    };


    private  NavigationManager.RerouteListener m_RouteListener = new NavigationManager.RerouteListener() {
        @Override
        public void onRerouteEnd(RouteResult routeResult) {
            super.onRerouteEnd(routeResult);
        }
    };


    private void initSettingsPanel() {
        m_settingsBtn = (ImageButton) m_activity.findViewById(R.id.settingButton);

        /* click settings panel button to open or close setting panel. */
        m_settingsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                m_settingsLayout = (LinearLayout) m_activity.findViewById(R.id.settingsPanelLayout);
                if (m_settingsLayout.getVisibility() == View.GONE) {
                    m_settingsLayout.setVisibility(View.VISIBLE);
                    if (m_settingsPanel == null) {
                        m_settingsPanel = new SettingsPanel(m_activity, m_map);
                    }
                } else {
                    m_settingsLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void cleanMap() {
        if (!m_mapObjectList.isEmpty()) {
            m_map.removeMapObjects(m_mapObjectList);
            m_mapObjectList.clear();
        }

    }



   /* @Override
    protected void onPause() {
        if (m_mapFragmentView != null) {
            m_mapFragmentView.pauseNlp();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (m_mapFragmentView != null) {
            m_mapFragmentView.resumeNlp();
        }
    }*/

   public void fetchDetails(View view){

       Intent intent= new Intent( m_activity, ListViewAdapter.class);
      //  Toast.makeText(m_activity,"Button Pressed", Toast.LENGTH_SHORT).show();
    //    context.startActivity(intent);



   }


    public void onDestroy() {
        /* Stop the navigation when app is destroyed */
        if (m_navigationManager != null) {
            m_navigationManager.stop();
            //m_map.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            m_navigationManager.setRoute(null);



        }
    }



}