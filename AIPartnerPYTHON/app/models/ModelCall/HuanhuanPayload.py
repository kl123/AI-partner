from pydantic import BaseModel

class HuanhuanPayload(BaseModel):
    timestamp_iso: str
    duration_min: float
    head_yaw_deg: float
    head_pitch_deg: float
    head_roll_deg: float
    gaze_on_screen_ratio: float
    blink_rate_per_min: float
    smile_prob: float
    brow_furrow_prob: float
    phone_usage_seconds: int
    interruptions_count: int
    slouch_score: float
    seat_moving_count: int
    fidgeting_score: float
    reading_speed_wpm: int
    writing_speed_wpm: int
    keystrokes_per_min: int
    env_noise_db: int
    light_lux: int
    breathing_rate_bpm: int
    tasks_planned: int
    tasks_completed: int