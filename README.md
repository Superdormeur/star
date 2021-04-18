# star

Question 2.
1. How to check if a video started during the time window?

    - Check timespan, (should be 0 if it's the first time a token is sent for this video session)
    - Check sessionDuration (should be less than timespan if a new video session is started)