expanding-peek-drawer
=====================

Create a view that animates in from the right.

The initial state should show part of the view ("peek" state).

In the "peek" state, the children of this view are not interactive.
Clicking anywhere on the view should expand the view to "fullscreen" state.
Clicking anywhere outside the view should dismiss the view entirely (perhaps with a buttonListener).

In "fullscreen" state, the children are active. There is a cross which will can dismiss the view entirely.
Pressing back should also dismiss the view entirely.

---

Maybe:

- since the drawer animates out with a slide from "peek" to "full-screen" then users expect it to be openable this way too (with a swipe)
- since the drawer animates out with a slide from "peek" to "full-screen" then users expect it to be closeable this way too (with a swipe). At this
point, children of the view are active and if there's a horizontal scroll in the children, then it could get awk - at the very least, the swipe would
need to start from

---

TODO:

- the animations and interactions seem fine for the drawer itself
-
