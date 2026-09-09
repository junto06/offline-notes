package com.mudassar.notes.auth.network

// Request tag marking a call TokenAuthenticator must never try to
// fix with a token refresh + retry - login and refresh itself
object SkipAuth
