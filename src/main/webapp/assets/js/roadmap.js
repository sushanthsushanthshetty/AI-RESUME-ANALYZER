// roadmap.js - Progress Tracking Logic

function updateTaskUI(taskId, progress, isCompleted, roadmapProgress, status, completedCount) {
    const bar = document.getElementById(`task-bar-${taskId}`);
    const percentText = document.getElementById(`task-percent-${taskId}`);
    const statusBadge = document.getElementById(`status-${taskId}`);
    const btn = document.getElementById(`btn-complete-${taskId}`);
    const card = document.getElementById(`task-${taskId}`);

    if (bar) {
        bar.style.width = `${progress}%`;
        bar.style.background = progress > 80 ? "var(--success)" : (progress > 50 ? "var(--warning)" : "var(--accent)");
    }
    if (percentText) percentText.textContent = `${progress}%`;
    
    if (statusBadge) {
        if (isCompleted) {
            statusBadge.className = "status-badge completed";
            statusBadge.innerHTML = '<i class="ti ti-circle-check"></i> Completed';
        } else {
            const statusClass = status.toLowerCase().replace(" ", "-");
            statusBadge.className = `status-badge ${statusClass}`;
            statusBadge.innerHTML = status === "At Risk" ? '<i class="ti ti-alert-triangle"></i> At Risk' : '<i class="ti ti-clock"></i> On Track';
        }
    }

    if (isCompleted && btn) {
        btn.textContent = "Task Completed";
        btn.disabled = true;
        if (card) card.classList.add('completed');
    }

    // Update Overall Roadmap Progress
    updateOverallProgress(roadmapProgress, completedCount);
}

function updateOverallProgress(percentage, completedCount) {
    const meterFill = document.querySelector('.progress-fill');
    const meterText = document.getElementById('overall-percent');
    const completedCountEl = document.getElementById('completed-count');
    
    if (meterFill) {
        const dashArray = 283;
        const dashOffset = dashArray - (dashArray * percentage / 100);
        meterFill.style.strokeDashoffset = dashOffset;
    }
    if (meterText) meterText.textContent = `${percentage}%`;
    if (completedCountEl && completedCount !== undefined) completedCountEl.textContent = completedCount;

    if (percentage === 100) {
        document.getElementById('overall-status-title').textContent = "Roadmap Completed! 🎉";
        document.getElementById('roadmap-status-text').textContent = "Completed";
        showCompletionAnimation();
    }
}

function toggleMilestone(roadmapId, taskId, milestone) {
    const params = new URLSearchParams();
    params.append('roadmapId', roadmapId);
    params.append('taskId', taskId);
    params.append('milestone', milestone);
    params.append('action', 'milestone');

    fetch('update-task-progress', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            updateTaskUI(data.taskId, data.progressPercentage, data.isCompleted, data.completionPercentage, data.status, data.completedCount);
            // Visual check
            event.target.closest('.milestone-item').classList.add('checked');
            event.target.disabled = true;
        }
    });
}

function markTaskComplete(roadmapId, taskId) {
    const params = new URLSearchParams();
    params.append('roadmapId', roadmapId);
    params.append('taskId', taskId);
    params.append('action', 'complete');

    fetch('update-task-progress', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    })
    .then(r => r.json())
    .then(data => {
        if (data.success) {
            updateTaskUI(data.taskId, data.progressPercentage, data.isCompleted, data.completionPercentage, data.status, data.completedCount);
            
            // Check off all milestones visually
            const card = document.getElementById(`task-${taskId}`);
            card.querySelectorAll('.milestone-item').forEach(m => {
                m.classList.add('checked');
                m.querySelector('input').checked = true;
                m.querySelector('input').disabled = true;
            });
        }
    });
}

function showCompletionAnimation() {
    setTimeout(() => {
        document.getElementById('completion-modal').style.display = 'flex';
    }, 1000);
}

function shareAchievement() {
    const text = `I just completed my skills roadmap for ${document.querySelector('.roadmap-title').textContent.replace(' Learning Roadmap', '')} using APEX! 🚀 #CareerGrowth #Learning`;
    const url = `https://www.linkedin.com/sharing/share-offsite/?text=${encodeURIComponent(text)}`;
    window.open(url, '_blank');
}
