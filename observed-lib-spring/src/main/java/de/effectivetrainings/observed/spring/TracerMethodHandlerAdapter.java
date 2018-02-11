package de.effectivetrainings.observed.spring;

import de.effectivetrainings.observed.TraceApplicationInfo;
import de.effectivetrainings.observed.TracerRequestLifeCycle;
import de.effectivetrainings.observed.impl.Headers;
import de.effectivetrainings.observed.impl.TracerContextProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class TracerMethodHandlerAdapter extends HandlerInterceptorAdapter {

    @NonNull
    private TracerRequestLifeCycle tracerRequestLifeCycle;
    @NonNull
    private TracerContextProvider tracerContextProvider;
    @NonNull
    private TraceApplicationInfo traceApplicationInfo;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final boolean result = super.preHandle(request, response, handler);
        tracerRequestLifeCycle.beforeRequest(tracerContextProvider.provide(Headers.of(request)));
        response.addHeader(TracerContextProvider.X_SOURCE, traceApplicationInfo.getName());
        return result;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        tracerRequestLifeCycle.afterRequest(tracerContextProvider.provide(Headers.of(request)));
    }
}
