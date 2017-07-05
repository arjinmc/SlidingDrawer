# SlidingDrawer
An Android SlidingDrawer. Sliding up to open,sliding down to close,also has auto rewind. Support normal layout,Listview,ScrollView,RecyclerView.

This SlidingDrawer can have only one child like ScrollView,if you have many children that you should add them as ScrollView way.

## sample image
![img](https://github.com/arjinmc/SlidingDrawer/blob/master/images/sample.gif) 

### layout.xml
```xml
<com.arjinmc.slidingdrawer.SlidingDrawer
        android:id="@+id/slidingdrawer"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_below="@id/ll_buttons"
        android:background="@android:color/holo_green_light">

        <!-- add your view here :start -->
        <android.support.v7.widget.RecyclerView
            android:id="@+id/recyclerview"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

         <!-- add your view here :end -->
    </com.arjinmc.slidingdrawer.SlidingDrawer>
```

## Methods
set closed position height
```java
setClosedPostionHeight(int height)
```
set open partly position height
```java
setPartlyPositionHeight(int height);
```
set auto rewind height that when touch move below this height,it will be rewinded.
```java
setAutoRewindHeight(int height);
```
init layout position  
You don't need to call this method onece before show up,unless the height of parent view changes frequently.
```java
initLayoutPosition();
```
set if need to click the top of SlidingDrawer to open it,defualt is true.
```java
setClickFirstChildToOpen(boolean toOpen)
```
set if openpartly should callback the OnScrollListener.onCurrentHeightChange,default is false.
```java
setOpenPartltCallbackChange(boolean callChange)
```
control the layout to open or close
```java
openPartly();
open();
close();
```
## Listeners
### OnStatusChangeListener
callback for the open,openPartly,close operations
### OnScrollListener
callback for the touch or animation running that the proportion of SlidingDrawer shown height to parent height. 
### OnFirstChildClickListener
callback for if has clicked the first child for RecyclerView
