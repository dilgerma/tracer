package de.effectivetrainings.observed.spring;

import de.effectivetrainings.observed.TracerContextHolder;
import de.effectivetrainings.observed.TracerRequestLifeCycle;
import de.effectivetrainings.observed.impl.Headers;
import de.effectivetrainings.observed.impl.TracerContextProvider;
import de.effectivetrainings.observed.model.TracerContext;
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
    private TracerContextHolder tracerContextHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final boolean result = super.preHandle(request, response, handler);
        final TracerContext context = tracerContextProvider.provide(Headers.of(request));
        //keep the context state because it contains timers.
        tracerContextHolder.set(context);
        tracerRequestLifeCycle.beforeRequest(context);
        return result;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        tracerContextHolder.get().ifPresent(context ->
                tracerRequestLifeCycle.afterRequest(tracerContextProvider.provide(Headers.of(request))));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        tracerContextHolder.clear();
    }
}
